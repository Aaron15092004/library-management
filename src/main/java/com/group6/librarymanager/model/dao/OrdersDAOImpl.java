package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Orders;
import com.group6.librarymanager.model.entity.Staff;
import com.group6.librarymanager.model.entity.Student;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrdersDAOImpl extends DBContext {

    public List<Orders> findAll() {
        List<Orders> list = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.StudentID, s.StudentName, o.StaffID, st.StaffName, " +
                "o.OrderDate, o.TotalAmount, o.Status " +
                "FROM Orders o " +
                "LEFT JOIN Student s ON o.StudentID = s.StudentID " +
                "LEFT JOIN Staff st ON o.StaffID = st.StaffID " +
                "ORDER BY o.OrderDate DESC";
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

    public Orders findById(Integer id) {
        String sql = "SELECT o.OrderID, o.StudentID, s.StudentName, o.StaffID, st.StaffName, " +
                "o.OrderDate, o.TotalAmount, o.Status " +
                "FROM Orders o " +
                "LEFT JOIN Student s ON o.StudentID = s.StudentID " +
                "LEFT JOIN Staff st ON o.StaffID = st.StaffID " +
                "WHERE o.OrderID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Orders mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("StudentID"));
        student.setStudentName(rs.getString("StudentName"));

        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("StaffID"));
        staff.setStaffName(rs.getString("StaffName"));

        Orders o = new Orders();
        o.setOrderId(rs.getInt("OrderID"));
        o.setStudent(student);
        o.setStaff(staff);
        Date d = rs.getDate("OrderDate");
        o.setOrderDate(d != null ? d.toLocalDate() : null);
        o.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        o.setStatus(rs.getString("Status"));
        return o;
    }

    public void insert(Orders o) {
        String sql = "INSERT INTO Orders (StudentID, StaffID, OrderDate, TotalAmount, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getStudent().getStudentId());
            ps.setInt(2, o.getStaff().getStaffId());
            ps.setDate(3, Date.valueOf(o.getOrderDate()));
            ps.setBigDecimal(4, o.getTotalAmount());
            ps.setString(5, o.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    o.setOrderId(keys.getInt(1));
                }
            }
            System.out.println("Inserted order ID: " + o.getOrderId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Orders o) {
        String sql = "UPDATE Orders SET StudentID = ?, StaffID = ?, OrderDate = ?, TotalAmount = ?, Status = ? WHERE OrderID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, o.getStudent().getStudentId());
            ps.setInt(2, o.getStaff().getStaffId());
            ps.setDate(3, Date.valueOf(o.getOrderDate()));
            ps.setBigDecimal(4, o.getTotalAmount());
            ps.setString(5, o.getStatus());
            ps.setInt(6, o.getOrderId());
            ps.executeUpdate();
            System.out.println("Updated order ID: " + o.getOrderId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Orders WHERE OrderID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted order ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Orders save(Orders o) {
        if (o.getOrderId() == null) {
            insert(o);
        } else {
            update(o);
        }
        return o;
    }

    public static void main(String[] args) {
        OrdersDAOImpl dao = new OrdersDAOImpl();

        // --- View all ---
        System.out.println("=== All Orders ===");
        List<Orders> list = dao.findAll();
        for (Orders o : list) {
            System.out.println(o.getOrderId()
                    + " | Student: " + (o.getStudent() != null ? o.getStudent().getStudentName() : "N/A")
                    + " | Staff: " + (o.getStaff() != null ? o.getStaff().getStaffName() : "N/A")
                    + " | Date: " + o.getOrderDate()
                    + " | Total: " + o.getTotalAmount()
                    + " | Status: " + o.getStatus());
        }
    }
}
