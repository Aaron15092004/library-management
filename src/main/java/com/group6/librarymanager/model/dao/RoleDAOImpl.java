package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Role;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleDAOImpl extends DBContext {

    public List<Role> findAll() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT RoleID, RoleName FROM Role ORDER BY RoleName";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleName(rs.getString("RoleName"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Role findById(Integer id) {
        String sql = "SELECT RoleID, RoleName FROM Role WHERE RoleID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role r = new Role();
                    r.setRoleId(rs.getInt("RoleID"));
                    r.setRoleName(rs.getString("RoleName"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Role r) {
        String sql = "INSERT INTO Role (RoleName) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.executeUpdate();
            System.out.println("Inserted role: " + r.getRoleName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Role r) {
        String sql = "UPDATE Role SET RoleName = ? WHERE RoleID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.setInt(2, r.getRoleId());
            ps.executeUpdate();
            System.out.println("Updated role ID: " + r.getRoleId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Role WHERE RoleID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted role ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Role save(Role r) {
        if (r.getRoleId() == null) {
            insert(r);
        } else {
            update(r);
        }
        return r;
    }

    public static void main(String[] args) {
        RoleDAOImpl dao = new RoleDAOImpl();

        // --- Insert ---
        Role r = new Role();
        r.setRoleName("Role Test");
        dao.insert(r);

        // --- View all ---
        System.out.println("=== All Roles ===");
        List<Role> list = dao.findAll();
        for (Role role : list) {
            System.out.println(role.getRoleId() + " - " + role.getRoleName());
        }
    }
}
