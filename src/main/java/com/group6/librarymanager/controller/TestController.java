package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.BookDAOImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final BookDAOImpl bookDAO;

    @GetMapping("/count")
    public String countBooks() {
        try {
            long count = bookDAO.count();
            return "Total books: " + count;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
