package com.group6.librarymanager.controller.api;

import com.group6.librarymanager.model.dao.*;
import com.group6.librarymanager.model.entity.Borrow;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final BorrowDAO borrowDAO;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        int totalBooks = bookDAO.findAll().stream()
                .mapToInt(b -> b.getQuantity() != null ? b.getQuantity() : 0)
                .sum();
        stats.put("totalBooks", totalBooks);

        stats.put("totalStudents", studentDAO.count());

        long activeBorrows = borrowDAO.findByStatus("Borrowing").size();
        stats.put("activeBorrows", activeBorrows);

        long overdueBorrows = borrowDAO.findByStatus("Overdue").size();
        stats.put("overdueBorrows", overdueBorrows);

        List<Map<String, Object>> recentBorrows = borrowDAO.findByStatus("Borrowing").stream()
                .sorted(Comparator.comparing(Borrow::getDueDate))
                .limit(5)
                .map(b -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("borrowId", b.getBorrowId());
                    m.put("studentName",
                            b.getStudent() != null ? b.getStudent().getStudentName() : "");
                    m.put("borrowDate",
                            b.getBorrowDate() != null ? b.getBorrowDate().toString() : "");
                    m.put("dueDate", b.getDueDate() != null ? b.getDueDate().toString() : "");
                    if (b.getBorrowItems() != null && !b.getBorrowItems().isEmpty()) {
                        m.put("bookName", b.getBorrowItems().get(0).getBook().getBookName());
                    }
                    return m;
                })
                .collect(Collectors.toList());
        stats.put("recentBorrows", recentBorrows);

        List<Map<String, Object>> overdueRecords = borrowDAO.findByStatus("Overdue").stream()
                .map(b -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("borrowId", b.getBorrowId());
                    m.put("studentName",
                            b.getStudent() != null ? b.getStudent().getStudentName() : "");
                    m.put("dueDate", b.getDueDate() != null ? b.getDueDate().toString() : "");
                    if (b.getBorrowItems() != null && !b.getBorrowItems().isEmpty()) {
                        m.put("bookName", b.getBorrowItems().get(0).getBook().getBookName());
                    }
                    return m;
                })
                .collect(Collectors.toList());
        stats.put("overdueRecords", overdueRecords);

        return stats;
    }
}
