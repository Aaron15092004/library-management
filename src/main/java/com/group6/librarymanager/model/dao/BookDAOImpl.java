package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.config.DBConnection;
import com.group6.librarymanager.model.entity.Book;
import com.group6.librarymanager.model.entity.Category;
import com.group6.librarymanager.model.entity.Publisher;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class BookDAOImpl extends DBConnection {
    private static final Logger LOGGER = Logger.getLogger(BookDAOImpl.class.getName());

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.BookID, b.BookName, b.Quantity, b.Available, " +
                     "b.CategoryID, c.CategoryName, " +
                     "b.PublisherID, p.PublisherName " +
                     "FROM Book b " +
                     "LEFT JOIN Category c ON b.CategoryID = c.CategoryID " +
                     "LEFT JOIN Publisher p ON b.PublisherID = p.PublisherID";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
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
                
                books.add(book);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all books", e);
        }
        
        return books;
    }

    public Book findById(Integer id) {
        String sql = "SELECT b.BookID, b.BookName, b.Quantity, b.Available, " +
                     "b.CategoryID, c.CategoryName, " +
                     "b.PublisherID, p.PublisherName " +
                     "FROM Book b " +
                     "LEFT JOIN Category c ON b.CategoryID = c.CategoryID " +
                     "LEFT JOIN Publisher p ON b.PublisherID = p.PublisherID " +
                     "WHERE b.BookID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching book with ID: " + id, e);
        }
        
        return null;
    }

    public Book save(Book book) {
        if (book.getBookId() == null) {
            return insert(book);
        } else {
            return update(book);
        }
    }

    private Book insert(Book book) {
        String sql = "INSERT INTO Book (BookName, Quantity, Available, CategoryID, PublisherID) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, book.getBookName());
            st.setInt(2, book.getQuantity());
            st.setInt(3, book.getAvailable());
            st.setInt(4, book.getCategory().getCategoryId());
            st.setInt(5, book.getPublisher().getPublisherId());
            
            int affectedRows = st.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setBookId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return book;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting book", e);
        }
        
        return null;
    }

    private Book update(Book book) {
        String sql = "UPDATE Book SET BookName = ?, Quantity = ?, Available = ?, " +
                     "CategoryID = ?, PublisherID = ? WHERE BookID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setString(1, book.getBookName());
            st.setInt(2, book.getQuantity());
            st.setInt(3, book.getAvailable());
            st.setInt(4, book.getCategory().getCategoryId());
            st.setInt(5, book.getPublisher().getPublisherId());
            st.setInt(6, book.getBookId());
            
            st.executeUpdate();
            return book;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating book", e);
        }
        
        return null;
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Book WHERE BookID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting book with ID: " + id, e);
        }
    }

    public List<Book> findByBookNameContainingIgnoreCase(String name) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.BookID, b.BookName, b.Quantity, b.Available, " +
                     "b.CategoryID, c.CategoryName, " +
                     "b.PublisherID, p.PublisherName " +
                     "FROM Book b " +
                     "LEFT JOIN Category c ON b.CategoryID = c.CategoryID " +
                     "LEFT JOIN Publisher p ON b.PublisherID = p.PublisherID " +
                     "WHERE b.BookName LIKE ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setString(1, "%" + name + "%");
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
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
                    
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching books by name", e);
        }
        
        return books;
    }

    public List<Book> findByCategoryId(Integer categoryId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.BookID, b.BookName, b.Quantity, b.Available, " +
                     "b.CategoryID, c.CategoryName, " +
                     "b.PublisherID, p.PublisherName " +
                     "FROM Book b " +
                     "LEFT JOIN Category c ON b.CategoryID = c.CategoryID " +
                     "LEFT JOIN Publisher p ON b.PublisherID = p.PublisherID " +
                     "WHERE b.CategoryID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, categoryId);
            
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
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
                    
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching books by category", e);
        }
        
        return books;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM Book";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting books", e);
        }
        
        return 0;
    }
}
