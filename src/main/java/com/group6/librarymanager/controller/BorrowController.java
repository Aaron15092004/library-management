package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.BookDAO;
import com.group6.librarymanager.model.dao.BorrowDAO;
import com.group6.librarymanager.model.dao.BorrowItemDAO;
import com.group6.librarymanager.model.dao.StaffDAO;
import com.group6.librarymanager.model.dao.StudentDAO;
import com.group6.librarymanager.model.entity.Borrow;
import com.group6.librarymanager.model.entity.BorrowItem;
import com.group6.librarymanager.model.entity.BorrowItemId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowDAO borrowDAO;
    private final BorrowItemDAO borrowItemDAO;
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final StaffDAO staffDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("borrows", borrowDAO.findAll());
        return "borrows/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Borrow borrow = borrowDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borrow not found: " + id));
        model.addAttribute("borrow", borrow);
        return "borrows/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("borrow", new Borrow());
        model.addAttribute("students", studentDAO.findAll());
        model.addAttribute("staffs", staffDAO.findAll());
        model.addAttribute("books", bookDAO.findByAvailableGreaterThan(0));
        return "borrows/form";
    }

    @PostMapping
    public String createBorrow(@Valid @ModelAttribute Borrow borrow,
            BindingResult result,
            @RequestParam(name = "bookIds", required = false) Integer[] bookIds,
            @RequestParam(name = "quantities", required = false) Integer[] quantities,
            Model model) {
        if (borrow.getStudent() == null || borrow.getStudent().getStudentId() == null) {
            result.rejectValue("student", "borrow.student.required", "Vui lòng chọn sinh viên.");
        }
        if (borrow.getStaff() == null || borrow.getStaff().getStaffId() == null) {
            result.rejectValue("staff", "borrow.staff.required", "Vui lòng chọn nhân viên.");
        }
        if (borrow.getBorrowDate() == null) {
            result.rejectValue("borrowDate", "borrow.borrowDate.required", "Vui lòng chọn ngày mượn.");
        }
        if (borrow.getDueDate() == null) {
            result.rejectValue("dueDate", "borrow.dueDate.required", "Vui lòng chọn hạn trả.");
        }
        if (borrow.getBorrowDate() != null && borrow.getDueDate() != null
                && borrow.getDueDate().isBefore(borrow.getBorrowDate())) {
            result.rejectValue("dueDate", "borrow.dueDate.invalid", "Hạn trả phải lớn hơn hoặc bằng ngày mượn.");
        }

        if (result.hasErrors()) {
            model.addAttribute("students", studentDAO.findAll());
            model.addAttribute("staffs", staffDAO.findAll());
            model.addAttribute("books", bookDAO.findByAvailableGreaterThan(0));
            return "borrows/form";
        }

        Borrow saved;
        try {
            borrow.setStatus("Borrowing");
            saved = borrowDAO.save(borrow);
            if (saved.getBorrowId() == null) {
                throw new IllegalStateException("Không tạo được phiếu mượn.");
            }
        } catch (Exception ex) {
            model.addAttribute("formError", "Lưu phiếu mượn thất bại: " + ex.getMessage());
            model.addAttribute("students", studentDAO.findAll());
            model.addAttribute("staffs", staffDAO.findAll());
            model.addAttribute("books", bookDAO.findByAvailableGreaterThan(0));
            return "borrows/form";
        }

        if (bookIds != null) {
            for (int i = 0; i < bookIds.length; i++) {
                int qty = (quantities != null && i < quantities.length) ? quantities[i] : 1;
                BorrowItem item = BorrowItem.builder()
                        .id(new BorrowItemId(saved.getBorrowId(), bookIds[i]))
                        .borrow(saved)
                        .book(bookDAO.findById(bookIds[i]).orElseThrow())
                        .quantity(qty)
                        .build();
                borrowItemDAO.save(item);

                bookDAO.findById(bookIds[i]).ifPresent(book -> {
                    book.setAvailable(book.getAvailable() - qty);
                    bookDAO.save(book);
                });
            }
        }
        return "redirect:/borrows";
    }

    @PostMapping("/{id}/return")
    public String returnBooks(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Borrow borrow = borrowDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borrow not found: " + id));

        if ("Returned".equals(borrow.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "This record has already been returned.");
            return "redirect:/borrows";
        }

        borrow.setStatus("Returned");
        borrow.setReturnDate(LocalDate.now());
        borrowDAO.save(borrow);

        borrow.getBorrowItems().forEach(item -> {
            item.getBook().setAvailable(item.getBook().getAvailable() + item.getQuantity());
            bookDAO.save(item.getBook());
        });

        return "redirect:/borrows";
    }
}
