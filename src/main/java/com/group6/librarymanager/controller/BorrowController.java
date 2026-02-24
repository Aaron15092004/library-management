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
        if (result.hasErrors()) {
            model.addAttribute("students", studentDAO.findAll());
            model.addAttribute("staffs", staffDAO.findAll());
            model.addAttribute("books", bookDAO.findByAvailableGreaterThan(0));
            return "borrows/form";
        }

        borrow.setStatus("Borrowing");
        Borrow saved = borrowDAO.save(borrow);

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
    public String returnBooks(@PathVariable Integer id) {
        Borrow borrow = borrowDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Borrow not found: " + id));

        borrow.setStatus("Returned");
        borrowDAO.save(borrow);

        borrow.getBorrowItems().forEach(item -> {
            item.getBook().setAvailable(item.getBook().getAvailable() + item.getQuantity());
            bookDAO.save(item.getBook());
        });

        return "redirect:/borrows";
    }
}
