package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.AuthorDAO;
import com.group6.librarymanager.model.dao.BookDAOImpl;
import com.group6.librarymanager.model.dao.CategoryDAOImpl;
import com.group6.librarymanager.model.dao.PublisherDAOImpl;
import com.group6.librarymanager.model.entity.Book;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookDAOImpl bookDAO;
    private final CategoryDAOImpl categoryDAO;
    private final PublisherDAOImpl publisherDAO;
    private final AuthorDAO authorDAO;

    // GET /books — list all books
    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("books", bookDAO.findAll());
            return "books/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    // GET /books/{id} — view detail
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Book book = bookDAO.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
        model.addAttribute("book", book);
        return "books/detail";
    }

    // GET /books/new — show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryDAO.findAll());
        model.addAttribute("publishers", publisherDAO.findAll());
        model.addAttribute("authors", authorDAO.findAll());
        return "books/form";
    }

    // POST /books — create book
    @PostMapping
    public String create(@Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryDAO.findAll());
            model.addAttribute("publishers", publisherDAO.findAll());
            model.addAttribute("authors", authorDAO.findAll());
            return "books/form";
        }

        // Set available = quantity if not set
        if (book.getAvailable() == null) {
            book.setAvailable(book.getQuantity());
        }

        bookDAO.save(book);
        return "redirect:/books";
    }

    // GET /books/{id}/edit — show edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Book book = bookDAO.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryDAO.findAll());
        model.addAttribute("publishers", publisherDAO.findAll());
        model.addAttribute("authors", authorDAO.findAll());
        return "books/form";
    }

    // POST /books/{id}/edit — update book
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute Book book,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryDAO.findAll());
            model.addAttribute("publishers", publisherDAO.findAll());
            model.addAttribute("authors", authorDAO.findAll());
            return "books/form";
        }
        book.setBookId(id);
        bookDAO.save(book);
        return "redirect:/books";
    }

    // POST /books/{id}/delete — delete book
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        bookDAO.deleteById(id);
        return "redirect:/books";
    }
}
