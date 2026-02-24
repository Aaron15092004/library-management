package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Student;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentDAOImpl extends DBContext {

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT StudentID, StudentName, Email, Phone FROM Student ORDER BY StudentName";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getInt("StudentID"));
                s.setStudentName(rs.getString("StudentName"));
                s.setEmail(rs.getString("Email"));
                s.setPhone(rs.getString("Phone"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Student findById(Integer id) {
        String sql = "SELECT StudentID, StudentName, Email, Phone FROM Student WHERE StudentID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("StudentID"));
                    s.setStudentName(rs.getString("StudentName"));
                    s.setEmail(rs.getString("Email"));
                    s.setPhone(rs.getString("Phone"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Student s) {
        String sql = "INSERT INTO Student (StudentName, Email, Phone) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.executeUpdate();
            System.out.println("Inserted student: " + s.getStudentName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Student s) {
        String sql = "UPDATE Student SET StudentName = ?, Email = ?, Phone = ? WHERE StudentID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setInt(4, s.getStudentId());
            ps.executeUpdate();
            System.out.println("Updated student ID: " + s.getStudentId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Student WHERE StudentID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted student ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Student save(Student s) {
        if (s.getStudentId() == null) {
            insert(s);
        } else {
            update(s);
        }
        return s;
    }

    public static void main(String[] args) {
        StudentDAOImpl dao = new StudentDAOImpl();

        // --- Insert ---
        Student s = new Student();
        s.setStudentName("Nguyen Van Test");
        s.setEmail("test@email.com");
        s.setPhone("0123456789");
        dao.insert(s);

        // --- View all ---
        System.out.println("=== All Students ===");
        List<Student> list = dao.findAll();
        for (Student st : list) {
            System.out.println(st.getStudentId() + " - " + st.getStudentName()
                    + " | Email: " + st.getEmail() + " | Phone: " + st.getPhone());
        }
    }
}
