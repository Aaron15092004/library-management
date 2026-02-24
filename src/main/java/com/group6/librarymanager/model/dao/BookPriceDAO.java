package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.BookPrice;
import com.group6.librarymanager.model.entity.BookPriceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookPriceDAO extends JpaRepository<BookPrice, BookPriceId> {
    List<BookPrice> findByBookBookId(Integer bookId);
}
