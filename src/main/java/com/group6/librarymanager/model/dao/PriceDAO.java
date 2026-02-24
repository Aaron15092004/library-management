package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceDAO extends JpaRepository<Price, Integer> {
}
