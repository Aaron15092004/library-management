package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Publisher;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PublisherDAOImpl extends DBContext {

    public List<Publisher> findAll() {
        List<Publisher> list = new ArrayList<>();
        String sql = "SELECT PublisherID, PublisherName FROM Publisher ORDER BY PublisherName";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Publisher p = new Publisher();
                p.setPublisherId(rs.getInt("PublisherID"));
                p.setPublisherName(rs.getString("PublisherName"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Publisher findById(Integer id) {
        String sql = "SELECT PublisherID, PublisherName FROM Publisher WHERE PublisherID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Publisher p = new Publisher();
                    p.setPublisherId(rs.getInt("PublisherID"));
                    p.setPublisherName(rs.getString("PublisherName"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Publisher p) {
        String sql = "INSERT INTO Publisher (PublisherName) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPublisherName());
            ps.executeUpdate();
            System.out.println("Inserted publisher: " + p.getPublisherName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Publisher p) {
        String sql = "UPDATE Publisher SET PublisherName = ? WHERE PublisherID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPublisherName());
            ps.setInt(2, p.getPublisherId());
            ps.executeUpdate();
            System.out.println("Updated publisher ID: " + p.getPublisherId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Publisher WHERE PublisherID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted publisher ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Publisher save(Publisher p) {
        if (p.getPublisherId() == null) {
            insert(p);
        } else {
            update(p);
        }
        return p;
    }

    public static void main(String[] args) {
        PublisherDAOImpl dao = new PublisherDAOImpl();

        // --- Insert ---
        Publisher p = new Publisher();
        p.setPublisherName("NXB Test Main");
        dao.insert(p);

        // --- View all ---
        System.out.println("=== All Publishers ===");
        List<Publisher> list = dao.findAll();
        for (Publisher pub : list) {
            System.out.println(pub.getPublisherId() + " - " + pub.getPublisherName());
        }
    }
}
