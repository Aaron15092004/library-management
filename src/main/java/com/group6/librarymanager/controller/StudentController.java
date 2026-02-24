package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.StudentDAO;
import com.group6.librarymanager.model.entity.Student;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentDAO studentDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentDAO.findAll());
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Student student, BindingResult result) {
        if (result.hasErrors())
            return "students/form";
        studentDAO.save(student);
        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Student student = studentDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
        model.addAttribute("student", student);
        return "students/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Student student,
            BindingResult result) {
        if (result.hasErrors())
            return "students/form";
        student.setStudentId(id);
        studentDAO.save(student);
        return "redirect:/students";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        studentDAO.deleteById(id);
        return "redirect:/students";
    }
}
