package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Author;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthorDAOImpl extends DBContext {

    public List<Author> findAll() {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT AuthorID, AuthorName FROM Author ORDER BY AuthorName";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Author a = new Author();
                a.setAuthorId(rs.getInt("AuthorID"));
                a.setAuthorName(rs.getString("AuthorName"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Author findById(Integer id) {
        String sql = "SELECT AuthorID, AuthorName FROM Author WHERE AuthorID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Author a = new Author();
                    a.setAuthorId(rs.getInt("AuthorID"));
                    a.setAuthorName(rs.getString("AuthorName"));
                    return a;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Author a) {
        String sql = "INSERT INTO Author (AuthorName) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAuthorName());
            ps.executeUpdate();
            System.out.println("Inserted author: " + a.getAuthorName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Author a) {
        String sql = "UPDATE Author SET AuthorName = ? WHERE AuthorID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAuthorName());
            ps.setInt(2, a.getAuthorId());
            ps.executeUpdate();
            System.out.println("Updated author ID: " + a.getAuthorId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Author WHERE AuthorID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted author ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Author save(Author a) {
        if (a.getAuthorId() == null) {
            insert(a);
        } else {
            update(a);
        }
        return a;
    }

    public static void main(String[] args) {
        AuthorDAOImpl dao = new AuthorDAOImpl();

        // --- Insert ---
        Author a = new Author();
        a.setAuthorName("Tac gia Test");
        dao.insert(a);

        // --- View all ---
        System.out.println("=== All Authors ===");
        List<Author> list = dao.findAll();
        for (Author au : list) {
            System.out.println(au.getAuthorId() + " - " + au.getAuthorName());
        }
    }
}
