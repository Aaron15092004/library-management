package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.PublisherDAOImpl;
import com.group6.librarymanager.model.entity.Publisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherDAOImpl publisherDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("publishers", publisherDAO.findAll());
        return "publishers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("publisher", new Publisher());
        return "publishers/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Publisher publisher, BindingResult result) {
        if (result.hasErrors()) {
            return "publishers/form";
        }
        publisherDAO.save(publisher);
        return "redirect:/publishers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Publisher publisher = publisherDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher not found: " + id));
        model.addAttribute("publisher", publisher);
        return "publishers/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Publisher publisher,
            BindingResult result) {
        if (result.hasErrors()) {
            return "publishers/form";
        }
        publisher.setPublisherId(id);
        publisherDAO.save(publisher);
        return "redirect:/publishers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        publisherDAO.deleteById(id);
        return "redirect:/publishers";
    }
}
