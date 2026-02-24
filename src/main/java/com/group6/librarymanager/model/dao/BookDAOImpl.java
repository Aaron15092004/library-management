package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Book;
import com.group6.librarymanager.model.entity.Category;
import com.group6.librarymanager.model.entity.Publisher;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookDAOImpl extends DBContext {

    private Book mapRow(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("CategoryID"));
        category.setCategoryName(rs.getString("CategoryName"));

        Publisher publisher = new Publisher();
        publisher.setPublisherId(rs.getInt("PublisherID"));
        publisher.setPublisherName(rs.getString("PublisherName"));

        Book book = new Book();
        book.setBookId(rs.getInt("BookID"));
        book.setBookName(rs.getString("BookName"));
        book.setQuantity(rs.getInt("Quantity"));
        book.setAvailable(rs.getInt("Available"));
        book.setCategory(category);
        book.setPublisher(publisher);
        return book;
    }

    private static final String SELECT_SQL = "SELECT b.BookID, b.BookName, b.Quantity, b.Available, " +
            "b.CategoryID, c.CategoryName, b.PublisherID, p.PublisherName " +
            "FROM Book b " +
            "LEFT JOIN Category c ON b.CategoryID = c.CategoryID " +
            "LEFT JOIN Publisher p ON b.PublisherID = p.PublisherID";

    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_SQL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Book findById(Integer id) {
        String sql = SELECT_SQL + " WHERE b.BookID = ?";
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

    public void insert(Book book) {
        String sql = "INSERT INTO Book (BookName, Quantity, Available, CategoryID, PublisherID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getBookName());
            ps.setInt(2, book.getQuantity());
            ps.setInt(3, book.getAvailable());
            ps.setInt(4, book.getCategory().getCategoryId());
            ps.setInt(5, book.getPublisher().getPublisherId());
            ps.executeUpdate();
            System.out.println("Inserted book: " + book.getBookName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Book book) {
        String sql = "UPDATE Book SET BookName = ?, Quantity = ?, Available = ?, CategoryID = ?, PublisherID = ? WHERE BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getBookName());
            ps.setInt(2, book.getQuantity());
            ps.setInt(3, book.getAvailable());
            ps.setInt(4, book.getCategory().getCategoryId());
            ps.setInt(5, book.getPublisher().getPublisherId());
            ps.setInt(6, book.getBookId());
            ps.executeUpdate();
            System.out.println("Updated book ID: " + book.getBookId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Book WHERE BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted book ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Book save(Book book) {
        if (book.getBookId() == null) {
            insert(book);
        } else {
            update(book);
        }
        return book;
    }

    public List<Book> findByBookNameContainingIgnoreCase(String name) {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_SQL + " WHERE b.BookName LIKE ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
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

    public List<Book> findByCategoryId(Integer categoryId) {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_SQL + " WHERE b.CategoryID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
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

    public long count() {
        String sql = "SELECT COUNT(*) FROM Book";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        BookDAOImpl dao = new BookDAOImpl();

        // --- View all ---
        System.out.println("=== All Books ===");
        List<Book> list = dao.findAll();
        for (Book b : list) {
            System.out.println(b.getBookId() + " - " + b.getBookName()
                    + " | Category: " + (b.getCategory() != null ? b.getCategory().getCategoryName() : "N/A")
                    + " | Publisher: " + (b.getPublisher() != null ? b.getPublisher().getPublisherName() : "N/A")
                    + " | Qty: " + b.getQuantity() + " | Available: " + b.getAvailable());
        }

        System.out.println("Total books: " + dao.count());
    }
}
