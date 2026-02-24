package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.RoleDAO;
import com.group6.librarymanager.model.dao.StaffDAO;
import com.group6.librarymanager.model.entity.Staff;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffDAO staffDAO;
    private final RoleDAO roleDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staffs", staffDAO.findAll());
        return "staffs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("roles", roleDAO.findAll());
        return "staffs/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Staff staff, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleDAO.findAll());
            return "staffs/form";
        }
        staffDAO.save(staff);
        return "redirect:/staffs";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Staff staff = staffDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + id));
        model.addAttribute("staff", staff);
        model.addAttribute("roles", roleDAO.findAll());
        return "staffs/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Staff staff,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleDAO.findAll());
            return "staffs/form";
        }
        staff.setStaffId(id);
        staffDAO.save(staff);
        return "redirect:/staffs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        staffDAO.deleteById(id);
        return "redirect:/staffs";
    }
}
