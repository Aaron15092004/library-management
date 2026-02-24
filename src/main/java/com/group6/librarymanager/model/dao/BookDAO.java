package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookDAO extends JpaRepository<Book, Integer> {
    List<Book> findByBookNameContainingIgnoreCase(String name);

    List<Book> findByCategory_CategoryId(Integer categoryId);

    List<Book> findByAvailableGreaterThan(Integer available);
}
