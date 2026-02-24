package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Book;
import com.group6.librarymanager.model.entity.OrderDetail;
import com.group6.librarymanager.model.entity.OrderDetailId;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDetailDAOImpl extends DBContext {

    public List<OrderDetail> findAll() {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.OrderID, od.BookID, b.BookName, od.Quantity, od.UnitPrice " +
                "FROM OrderDetail od " +
                "LEFT JOIN Book b ON od.BookID = b.BookID";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderDetail> findByOrderId(Integer orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.OrderID, od.BookID, b.BookName, od.Quantity, od.UnitPrice " +
                "FROM OrderDetail od " +
                "LEFT JOIN Book b ON od.BookID = b.BookID " +
                "WHERE od.OrderID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private OrderDetail mapRow(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("BookID"));
        book.setBookName(rs.getString("BookName"));

        OrderDetail od = new OrderDetail();
        od.setId(new OrderDetailId(rs.getInt("OrderID"), rs.getInt("BookID")));
        od.setBook(book);
        od.setQuantity(rs.getInt("Quantity"));
        od.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        return od;
    }

    public void insert(OrderDetail od) {
        String sql = "INSERT INTO OrderDetail (OrderID, BookID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, od.getId().getOrderId());
            ps.setInt(2, od.getId().getBookId());
            ps.setInt(3, od.getQuantity());
            ps.setBigDecimal(4, od.getUnitPrice());
            ps.executeUpdate();
            System.out.println(
                    "Inserted order detail: OrderID=" + od.getId().getOrderId() + ", BookID=" + od.getId().getBookId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(OrderDetail od) {
        String sql = "UPDATE OrderDetail SET Quantity = ?, UnitPrice = ? WHERE OrderID = ? AND BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, od.getQuantity());
            ps.setBigDecimal(2, od.getUnitPrice());
            ps.setInt(3, od.getId().getOrderId());
            ps.setInt(4, od.getId().getBookId());
            ps.executeUpdate();
            System.out.println(
                    "Updated order detail: OrderID=" + od.getId().getOrderId() + ", BookID=" + od.getId().getBookId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer orderId, Integer bookId) {
        String sql = "DELETE FROM OrderDetail WHERE OrderID = ? AND BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, bookId);
            ps.executeUpdate();
            System.out.println("Deleted order detail: OrderID=" + orderId + ", BookID=" + bookId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(OrderDetail od) {
        // Try update first, if no rows affected then insert
        String sql = "UPDATE OrderDetail SET Quantity = ?, UnitPrice = ? WHERE OrderID = ? AND BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, od.getQuantity());
            ps.setBigDecimal(2, od.getUnitPrice());
            ps.setInt(3, od.getId().getOrderId());
            ps.setInt(4, od.getId().getBookId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                insert(od);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OrderDetailDAOImpl dao = new OrderDetailDAOImpl();

        // --- View all ---
        System.out.println("=== All Order Details ===");
        List<OrderDetail> list = dao.findAll();
        for (OrderDetail od : list) {
            System.out.println("OrderID: " + od.getId().getOrderId()
                    + " | BookID: " + od.getId().getBookId()
                    + " | Book: " + (od.getBook() != null ? od.getBook().getBookName() : "N/A")
                    + " | Qty: " + od.getQuantity()
                    + " | Price: " + od.getUnitPrice());
        }
    }
}
