package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.model.entity.Book;
import com.group6.librarymanager.model.entity.BorrowItem;
import com.group6.librarymanager.model.entity.BorrowItemId;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BorrowItemDAOImpl extends DBContext {

    public List<BorrowItem> findAll() {
        List<BorrowItem> list = new ArrayList<>();
        String sql = "SELECT bi.BorrowID, bi.BookID, b.BookName, bi.Quantity " +
                "FROM BorrowItem bi " +
                "LEFT JOIN Book b ON bi.BookID = b.BookID";
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

    public List<BorrowItem> findByBorrowId(Integer borrowId) {
        List<BorrowItem> list = new ArrayList<>();
        String sql = "SELECT bi.BorrowID, bi.BookID, b.BookName, bi.Quantity " +
                "FROM BorrowItem bi " +
                "LEFT JOIN Book b ON bi.BookID = b.BookID " +
                "WHERE bi.BorrowID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
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

    private BorrowItem mapRow(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("BookID"));
        book.setBookName(rs.getString("BookName"));

        BorrowItem bi = new BorrowItem();
        bi.setId(new BorrowItemId(rs.getInt("BorrowID"), rs.getInt("BookID")));
        bi.setBook(book);
        bi.setQuantity(rs.getInt("Quantity"));
        return bi;
    }

    public void insert(BorrowItem bi) {
        String sql = "INSERT INTO BorrowItem (BorrowID, BookID, Quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bi.getId().getBorrowId());
            ps.setInt(2, bi.getId().getBookId());
            ps.setInt(3, bi.getQuantity());
            ps.executeUpdate();
            System.out.println("Inserted borrow item: BorrowID=" + bi.getId().getBorrowId() + ", BookID="
                    + bi.getId().getBookId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(BorrowItem bi) {
        String sql = "UPDATE BorrowItem SET Quantity = ? WHERE BorrowID = ? AND BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bi.getQuantity());
            ps.setInt(2, bi.getId().getBorrowId());
            ps.setInt(3, bi.getId().getBookId());
            ps.executeUpdate();
            System.out.println(
                    "Updated borrow item: BorrowID=" + bi.getId().getBorrowId() + ", BookID=" + bi.getId().getBookId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Integer borrowId, Integer bookId) {
        String sql = "DELETE FROM BorrowItem WHERE BorrowID = ? AND BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            ps.setInt(2, bookId);
            ps.executeUpdate();
            System.out.println("Deleted borrow item: BorrowID=" + borrowId + ", BookID=" + bookId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(BorrowItem bi) {
        String sql = "UPDATE BorrowItem SET Quantity = ? WHERE BorrowID = ? AND BookID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bi.getQuantity());
            ps.setInt(2, bi.getId().getBorrowId());
            ps.setInt(3, bi.getId().getBookId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                insert(bi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BorrowItemDAOImpl dao = new BorrowItemDAOImpl();

        // --- View all ---
        System.out.println("=== All Borrow Items ===");
        List<BorrowItem> list = dao.findAll();
        for (BorrowItem bi : list) {
            System.out.println("BorrowID: " + bi.getId().getBorrowId()
                    + " | BookID: " + bi.getId().getBookId()
                    + " | Book: " + (bi.getBook() != null ? bi.getBook().getBookName() : "N/A")
                    + " | Qty: " + bi.getQuantity());
        }
    }
}
