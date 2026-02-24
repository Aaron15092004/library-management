package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookFileDAO extends JpaRepository<BookFile, Integer> {
    List<BookFile> findByBookBookIdAndIsActiveTrue(Integer bookId);
}
