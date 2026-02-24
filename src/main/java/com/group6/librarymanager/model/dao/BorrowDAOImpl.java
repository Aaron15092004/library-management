package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Borrow;
import com.group6.librarymanager.model.entity.Staff;
import com.group6.librarymanager.model.entity.Student;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowDAOImpl extends DBContext {

    public List<Borrow> findAll() {
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT b.BorrowID, b.StudentID, s.StudentName, b.StaffID, st.StaffName, " +
                "b.BorrowDate, b.DueDate, b.Status " +
                "FROM Borrow b " +
                "LEFT JOIN Student s ON b.StudentID = s.StudentID " +
                "LEFT JOIN Staff st ON b.StaffID = st.StaffID " +
                "ORDER BY b.BorrowDate DESC";
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

    public Borrow findById(Integer id) {
        String sql = "SELECT b.BorrowID, b.StudentID, s.StudentName, b.StaffID, st.StaffName, " +
                "b.BorrowDate, b.DueDate, b.Status " +
                "FROM Borrow b " +
                "LEFT JOIN Student s ON b.StudentID = s.StudentID " +
                "LEFT JOIN Staff st ON b.StaffID = st.StaffID " +
                "WHERE b.BorrowID = ?";
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

    private Borrow mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("StudentID"));
        student.setStudentName(rs.getString("StudentName"));

        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("StaffID"));
        staff.setStaffName(rs.getString("StaffName"));

        Borrow b = new Borrow();
        b.setBorrowId(rs.getInt("BorrowID"));
        b.setStudent(student);
        b.setStaff(staff);
        Date bd = rs.getDate("BorrowDate");
        b.setBorrowDate(bd != null ? bd.toLocalDate() : null);
        Date dd = rs.getDate("DueDate");
        b.setDueDate(dd != null ? dd.toLocalDate() : null);
        b.setStatus(rs.getString("Status"));
        return b;
    }

    public void insert(Borrow b) {
        String sql = "INSERT INTO Borrow (StudentID, StaffID, BorrowDate, DueDate, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getStudent().getStudentId());
            ps.setInt(2, b.getStaff().getStaffId());
            ps.setDate(3, Date.valueOf(b.getBorrowDate()));
            ps.setDate(4, Date.valueOf(b.getDueDate()));
            ps.setString(5, b.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    b.setBorrowId(keys.getInt(1));
                }
            }
            System.out.println("Inserted borrow ID: " + b.getBorrowId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Borrow b) {
        String sql = "UPDATE Borrow SET StudentID = ?, StaffID = ?, BorrowDate = ?, DueDate = ?, Status = ? WHERE BorrowID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getStudent().getStudentId());
            ps.setInt(2, b.getStaff().getStaffId());
            ps.setDate(3, Date.valueOf(b.getBorrowDate()));
            ps.setDate(4, Date.valueOf(b.getDueDate()));
            ps.setString(5, b.getStatus());
            ps.setInt(6, b.getBorrowId());
            ps.executeUpdate();
            System.out.println("Updated borrow ID: " + b.getBorrowId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Borrow WHERE BorrowID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted borrow ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Borrow save(Borrow b) {
        if (b.getBorrowId() == null) {
            insert(b);
        } else {
            update(b);
        }
        return b;
    }

    public static void main(String[] args) {
        BorrowDAOImpl dao = new BorrowDAOImpl();

        // --- View all ---
        System.out.println("=== All Borrows ===");
        List<Borrow> list = dao.findAll();
        for (Borrow b : list) {
            System.out.println(b.getBorrowId()
                    + " | Student: " + (b.getStudent() != null ? b.getStudent().getStudentName() : "N/A")
                    + " | Staff: " + (b.getStaff() != null ? b.getStaff().getStaffName() : "N/A")
                    + " | BorrowDate: " + b.getBorrowDate()
                    + " | DueDate: " + b.getDueDate()
                    + " | Status: " + b.getStatus());
        }
    }
}
