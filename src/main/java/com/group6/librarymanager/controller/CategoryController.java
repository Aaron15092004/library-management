package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.CategoryDAO;
import com.group6.librarymanager.model.entity.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryDAO categoryDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryDAO.findAll());
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Category category, BindingResult result) {
        if (result.hasErrors())
            return "categories/form";
        categoryDAO.save(category);
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        model.addAttribute("category", category);
        return "categories/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Category category,
            BindingResult result) {
        if (result.hasErrors())
            return "categories/form";
        category.setCategoryId(id);
        categoryDAO.save(category);
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        categoryDAO.deleteById(id);
        return "redirect:/categories";
    }
}
