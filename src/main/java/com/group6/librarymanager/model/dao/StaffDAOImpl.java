package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Staff;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StaffDAOImpl extends DBContext {

    public List<Staff> findAll() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT StaffID, StaffName FROM Staff ORDER BY StaffName";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Staff s = new Staff();
                s.setStaffId(rs.getInt("StaffID"));
                s.setStaffName(rs.getString("StaffName"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Staff findById(Integer id) {
        String sql = "SELECT StaffID, StaffName FROM Staff WHERE StaffID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff s = new Staff();
                    s.setStaffId(rs.getInt("StaffID"));
                    s.setStaffName(rs.getString("StaffName"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Staff s) {
        String sql = "INSERT INTO Staff (StaffName) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStaffName());
            ps.executeUpdate();
            System.out.println("Inserted staff: " + s.getStaffName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Staff s) {
        String sql = "UPDATE Staff SET StaffName = ? WHERE StaffID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStaffName());
            ps.setInt(2, s.getStaffId());
            ps.executeUpdate();
            System.out.println("Updated staff ID: " + s.getStaffId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Staff WHERE StaffID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted staff ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Staff save(Staff s) {
        if (s.getStaffId() == null) {
            insert(s);
        } else {
            update(s);
        }
        return s;
    }

    public static void main(String[] args) {
        StaffDAOImpl dao = new StaffDAOImpl();

        // --- Insert ---
        Staff s = new Staff();
        s.setStaffName("Nhan vien Test");
        dao.insert(s);

        // --- View all ---
        System.out.println("=== All Staff ===");
        List<Staff> list = dao.findAll();
        for (Staff st : list) {
            System.out.println(st.getStaffId() + " - " + st.getStaffName());
        }
    }
}
