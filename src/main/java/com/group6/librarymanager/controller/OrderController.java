package com.group6.librarymanager.controller;

import com.group6.librarymanager.model.dao.BookDAO;
import com.group6.librarymanager.model.dao.OrderDetailDAO;
import com.group6.librarymanager.model.dao.OrdersDAO;
import com.group6.librarymanager.model.dao.StaffDAO;
import com.group6.librarymanager.model.dao.StudentDAO;
import com.group6.librarymanager.model.entity.OrderDetail;
import com.group6.librarymanager.model.entity.OrderDetailId;
import com.group6.librarymanager.model.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrdersDAO ordersDAO;
    private final OrderDetailDAO orderDetailDAO;
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final StaffDAO staffDAO;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", ordersDAO.findAll());
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Orders order = ordersDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        model.addAttribute("order", order);
        return "orders/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Orders());
        model.addAttribute("students", studentDAO.findAll());
        model.addAttribute("staffs", staffDAO.findAll());
        model.addAttribute("books", bookDAO.findByAvailableGreaterThan(0));
        return "orders/form";
    }

    @PostMapping
    public String createOrder(@ModelAttribute Orders order,
            @RequestParam(name = "bookIds", required = false) Integer[] bookIds,
            @RequestParam(name = "quantities", required = false) Integer[] quantities,
            @RequestParam(name = "unitPrices", required = false) BigDecimal[] unitPrices) {
        order.setOrderDate(LocalDate.now());

        BigDecimal total = BigDecimal.ZERO;
        Orders saved = ordersDAO.save(order);

        if (bookIds != null) {
            for (int i = 0; i < bookIds.length; i++) {
                int qty = (quantities != null && i < quantities.length) ? quantities[i] : 1;
                BigDecimal price = (unitPrices != null && i < unitPrices.length) ? unitPrices[i] : BigDecimal.ZERO;

                OrderDetail detail = OrderDetail.builder()
                        .id(new OrderDetailId(saved.getOrderId(), bookIds[i]))
                        .orders(saved)
                        .book(bookDAO.findById(bookIds[i]).orElseThrow())
                        .quantity(qty)
                        .unitPrice(price)
                        .build();
                orderDetailDAO.save(detail);

                total = total.add(price.multiply(BigDecimal.valueOf(qty)));

                bookDAO.findById(bookIds[i]).ifPresent(book -> {
                    book.setAvailable(book.getAvailable() - qty);
                    bookDAO.save(book);
                });
            }
        }

        saved.setTotalAmount(total);
        ordersDAO.save(saved);

        return "redirect:/orders";
    }
}
