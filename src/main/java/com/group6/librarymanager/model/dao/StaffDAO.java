package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffDAO extends JpaRepository<Staff, Integer> {
    List<Staff> findByStaffNameContainingIgnoreCase(String name);
}
