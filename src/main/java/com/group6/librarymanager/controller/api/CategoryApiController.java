package com.group6.librarymanager.controller.api;

import com.group6.librarymanager.model.dao.CategoryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryDAO categoryDAO;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return categoryDAO.findAll().stream().map(c -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("categoryId", c.getCategoryId());
            map.put("categoryName", c.getCategoryName());
            return map;
        }).collect(Collectors.toList());
    }
}
