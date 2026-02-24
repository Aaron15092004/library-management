package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.OrderDetail;
import com.group6.librarymanager.model.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailDAO extends JpaRepository<OrderDetail, OrderDetailId> {
    List<OrderDetail> findByOrdersOrderId(Integer orderId);
}
