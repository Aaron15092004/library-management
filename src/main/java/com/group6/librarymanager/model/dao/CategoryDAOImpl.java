package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Category;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryDAOImpl extends DBContext {

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT CategoryID, CategoryName FROM Category ORDER BY CategoryName";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("CategoryID"));
                c.setCategoryName(rs.getString("CategoryName"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Category findById(Integer id) {
        String sql = "SELECT CategoryID, CategoryName FROM Category WHERE CategoryID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = new Category();
                    c.setCategoryId(rs.getInt("CategoryID"));
                    c.setCategoryName(rs.getString("CategoryName"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Category c) {
        String sql = "INSERT INTO Category (CategoryName) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryName());
            ps.executeUpdate();
            System.out.println("Inserted category: " + c.getCategoryName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Category c) {
        String sql = "UPDATE Category SET CategoryName = ? WHERE CategoryID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryName());
            ps.setInt(2, c.getCategoryId());
            ps.executeUpdate();
            System.out.println("Updated category ID: " + c.getCategoryId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted category ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Category save(Category c) {
        if (c.getCategoryId() == null) {
            insert(c);
        } else {
            update(c);
        }
        return c;
    }

    public static void main(String[] args) {
        CategoryDAOImpl dao = new CategoryDAOImpl();

        // --- Insert ---
        Category c = new Category();
        c.setCategoryName("The loai Test");
        dao.insert(c);

        // --- View all ---
        System.out.println("=== All Categories ===");
        List<Category> list = dao.findAll();
        for (Category cat : list) {
            System.out.println(cat.getCategoryId() + " - " + cat.getCategoryName());
        }
    }
}
