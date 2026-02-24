package com.group6.librarymanager.controller.api;

import com.group6.librarymanager.model.dao.*;
import com.group6.librarymanager.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrdersDAO ordersDAO;
    private final OrderDetailDAO orderDetailDAO;
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final StaffDAO staffDAO;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return ordersDAO.findAll().stream().map(this::toMap).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Integer studentId = (Integer) body.get("studentId");
            Integer staffId = (Integer) body.get("staffId");

            Student student = studentDAO.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            Staff staff = staffDAO.findById(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

            Orders order = Orders.builder()
                    .student(student)
                    .staff(staff)
                    .orderDate(LocalDate.now())
                    .totalAmount(BigDecimal.ZERO)
                    .status("Completed")
                    .build();
            Orders saved = ordersDAO.save(order);

            BigDecimal total = BigDecimal.ZERO;

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    Integer bookId = (Integer) item.get("bookId");
                    Integer qty = (Integer) item.getOrDefault("quantity", 1);
                    BigDecimal unitPrice = new BigDecimal(item.get("unitPrice").toString());

                    Book book = bookDAO.findById(bookId).orElseThrow();
                    OrderDetail detail = OrderDetail.builder()
                            .id(new OrderDetailId(saved.getOrderId(), bookId))
                            .orders(saved)
                            .book(book)
                            .quantity(qty)
                            .unitPrice(unitPrice)
                            .build();
                    orderDetailDAO.save(detail);

                    total = total.add(unitPrice.multiply(BigDecimal.valueOf(qty)));

                    book.setAvailable(book.getAvailable() - qty);
                    bookDAO.save(book);
                }
            }

            saved.setTotalAmount(total);
            ordersDAO.save(saved);

            return ResponseEntity.ok(toMap(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toMap(Orders o) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("orderId", o.getOrderId());
        map.put("orderDate", o.getOrderDate() != null ? o.getOrderDate().toString() : null);
        map.put("totalAmount", o.getTotalAmount());
        map.put("status", o.getStatus());

        if (o.getStudent() != null) {
            map.put("studentId", o.getStudent().getStudentId());
            map.put("studentName", o.getStudent().getStudentName());
        }
        if (o.getStaff() != null) {
            map.put("staffId", o.getStaff().getStaffId());
            map.put("staffName", o.getStaff().getStaffName());
        }

        if (o.getOrderDetails() != null) {
            List<Map<String, Object>> details = new ArrayList<>();
            for (OrderDetail d : o.getOrderDetails()) {
                Map<String, Object> dm = new LinkedHashMap<>();
                dm.put("bookId", d.getBook().getBookId());
                dm.put("bookName", d.getBook().getBookName());
                dm.put("quantity", d.getQuantity());
                dm.put("unitPrice", d.getUnitPrice());
                details.add(dm);
            }
            map.put("items", details);
        }

        return map;
    }
}
