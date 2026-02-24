package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.BookCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCodeDAO extends JpaRepository<BookCode, Integer> {
    List<BookCode> findByBookBookId(Integer bookId);

    Optional<BookCode> findByBookCode(String bookCode);
}
