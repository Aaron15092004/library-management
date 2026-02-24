package com.group6.librarymanager.controller.api;

import com.group6.librarymanager.model.dao.BookDAO;
import com.group6.librarymanager.model.dao.CategoryDAO;
import com.group6.librarymanager.model.dao.PublisherDAO;
import com.group6.librarymanager.model.entity.Author;
import com.group6.librarymanager.model.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookApiController {

    private final BookDAO bookDAO;
    private final CategoryDAO categoryDAO;
    private final PublisherDAO publisherDAO;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return bookDAO.findAll().stream().map(this::toMap).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return bookDAO.findById(id)
                .map(book -> ResponseEntity.ok(toMap(book)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Book book = new Book();
        book.setBookName((String) body.get("bookName"));
        book.setQuantity((Integer) body.getOrDefault("quantity", 0));
        book.setAvailable((Integer) body.getOrDefault("available", 0));

        Integer categoryId = (Integer) body.get("categoryId");
        if (categoryId != null) {
            categoryDAO.findById(categoryId).ifPresent(book::setCategory);
        }
        Integer publisherId = (Integer) body.get("publisherId");
        if (publisherId != null) {
            publisherDAO.findById(publisherId).ifPresent(book::setPublisher);
        }

        Book saved = bookDAO.save(book);
        return ResponseEntity.ok(toMap(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!bookDAO.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookDAO.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> toMap(Book book) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("bookId", book.getBookId());
        map.put("bookName", book.getBookName());
        map.put("quantity", book.getQuantity());
        map.put("available", book.getAvailable());

        if (book.getCategory() != null) {
            map.put("categoryId", book.getCategory().getCategoryId());
            map.put("categoryName", book.getCategory().getCategoryName());
        }
        if (book.getPublisher() != null) {
            map.put("publisherId", book.getPublisher().getPublisherId());
            map.put("publisherName", book.getPublisher().getPublisherName());
        }

        if (book.getAuthors() != null) {
            List<Map<String, Object>> authors = new ArrayList<>();
            for (Author a : book.getAuthors()) {
                Map<String, Object> am = new LinkedHashMap<>();
                am.put("authorId", a.getAuthorId());
                am.put("authorName", a.getAuthorName());
                authors.add(am);
            }
            map.put("authors", authors);
            map.put("authorNames", book.getAuthors().stream()
                    .map(Author::getAuthorName)
                    .collect(Collectors.joining(", ")));
        }

        int available = book.getAvailable() != null ? book.getAvailable() : 0;
        int quantity = book.getQuantity() != null ? book.getQuantity() : 0;
        if (available == 0) {
            map.put("status", "out");
        } else if (available <= quantity * 0.2) {
            map.put("status", "low");
        } else {
            map.put("status", "available");
        }

        return map;
    }
}
