package com.group6.librarymanager.controller.api;

import com.group6.librarymanager.model.dao.*;
import com.group6.librarymanager.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowApiController {

    private final BorrowDAO borrowDAO;
    private final BorrowItemDAO borrowItemDAO;
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final StaffDAO staffDAO;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return borrowDAO.findAll().stream().map(this::toMap).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Integer studentId = (Integer) body.get("studentId");
            Integer staffId = (Integer) body.get("staffId");
            String dueDateStr = (String) body.get("dueDate");

            Student student = studentDAO.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            Staff staff = staffDAO.findById(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

            Borrow borrow = Borrow.builder()
                    .student(student)
                    .staff(staff)
                    .borrowDate(LocalDate.now())
                    .dueDate(LocalDate.parse(dueDateStr))
                    .status("Borrowing")
                    .build();
            Borrow saved = borrowDAO.save(borrow);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    Integer bookId = (Integer) item.get("bookId");
                    Integer qty = (Integer) item.getOrDefault("quantity", 1);

                    Book book = bookDAO.findById(bookId).orElseThrow();
                    BorrowItem borrowItem = BorrowItem.builder()
                            .id(new BorrowItemId(saved.getBorrowId(), bookId))
                            .borrow(saved)
                            .book(book)
                            .quantity(qty)
                            .build();
                    borrowItemDAO.save(borrowItem);

                    book.setAvailable(book.getAvailable() - qty);
                    bookDAO.save(book);
                }
            }

            return ResponseEntity.ok(toMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBooks(@PathVariable Integer id) {
        return borrowDAO.findById(id).map(borrow -> {
            borrow.setStatus("Returned");
            borrowDAO.save(borrow);

            if (borrow.getBorrowItems() != null) {
                borrow.getBorrowItems().forEach(item -> {
                    item.getBook().setAvailable(item.getBook().getAvailable() + item.getQuantity());
                    bookDAO.save(item.getBook());
                });
            }

            return ResponseEntity.ok(toMap(borrow));
        }).orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toMap(Borrow b) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("borrowId", b.getBorrowId());
        map.put("borrowDate", b.getBorrowDate() != null ? b.getBorrowDate().toString() : null);
        map.put("dueDate", b.getDueDate() != null ? b.getDueDate().toString() : null);
        map.put("status", b.getStatus());

        if (b.getStudent() != null) {
            map.put("studentId", b.getStudent().getStudentId());
            map.put("studentName", b.getStudent().getStudentName());
        }
        if (b.getStaff() != null) {
            map.put("staffId", b.getStaff().getStaffId());
            map.put("staffName", b.getStaff().getStaffName());
        }

        if (b.getBorrowItems() != null) {
            List<Map<String, Object>> items = new ArrayList<>();
            for (BorrowItem bi : b.getBorrowItems()) {
                Map<String, Object> im = new LinkedHashMap<>();
                im.put("bookId", bi.getBook().getBookId());
                im.put("bookName", bi.getBook().getBookName());
                im.put("quantity", bi.getQuantity());
                items.add(im);
            }
            map.put("items", items);
        }

        return map;
    }
}
