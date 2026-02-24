package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersDAO extends JpaRepository<Orders, Integer> {
    List<Orders> findByStudentStudentId(Integer studentId);
}
