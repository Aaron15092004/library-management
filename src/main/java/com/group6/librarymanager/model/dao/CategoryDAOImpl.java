package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.config.DBConnection;
import com.group6.librarymanager.model.entity.Category;
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
public class CategoryDAOImpl extends DBConnection {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAOImpl.class.getName());

    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT CategoryID, CategoryName FROM Category ORDER BY CategoryName";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("CategoryID"));
                category.setCategoryName(rs.getString("CategoryName"));
                categories.add(category);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all categories", e);
        }
        
        return categories;
    }

    public Category findById(Integer id) {
        String sql = "SELECT CategoryID, CategoryName FROM Category WHERE CategoryID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setCategoryId(rs.getInt("CategoryID"));
                    category.setCategoryName(rs.getString("CategoryName"));
                    return category;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching category with ID: " + id, e);
        }
        
        return null;
    }

    public Category save(Category category) {
        if (category.getCategoryId() == null) {
            return insert(category);
        } else {
            return update(category);
        }
    }

    private Category insert(Category category) {
        String sql = "INSERT INTO Category (CategoryName) VALUES (?)";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, category.getCategoryName());
            
            int affectedRows = st.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setCategoryId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return category;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting category", e);
        }
        
        return null;
    }

    private Category update(Category category) {
        String sql = "UPDATE Category SET CategoryName = ? WHERE CategoryID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setString(1, category.getCategoryName());
            st.setInt(2, category.getCategoryId());
            
            st.executeUpdate();
            return category;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category", e);
        }
        
        return null;
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category with ID: " + id, e);
        }
    }
}
