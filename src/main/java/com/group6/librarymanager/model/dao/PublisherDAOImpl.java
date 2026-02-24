package com.group6.librarymanager.model.dao;

import com.group6.librarymanager.config.DBConnection;
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
public class PublisherDAOImpl extends DBConnection {
    private static final Logger LOGGER = Logger.getLogger(PublisherDAOImpl.class.getName());

    public List<Publisher> findAll() {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT PublisherID, PublisherName FROM Publisher ORDER BY PublisherName";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.setPublisherId(rs.getInt("PublisherID"));
                publisher.setPublisherName(rs.getString("PublisherName"));
                publishers.add(publisher);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all publishers", e);
        }
        
        return publishers;
    }

    public Publisher findById(Integer id) {
        String sql = "SELECT PublisherID, PublisherName FROM Publisher WHERE PublisherID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Publisher publisher = new Publisher();
                    publisher.setPublisherId(rs.getInt("PublisherID"));
                    publisher.setPublisherName(rs.getString("PublisherName"));
                    return publisher;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching publisher with ID: " + id, e);
        }
        
        return null;
    }

    public Publisher save(Publisher publisher) {
        if (publisher.getPublisherId() == null) {
            return insert(publisher);
        } else {
            return update(publisher);
        }
    }

    private Publisher insert(Publisher publisher) {
        String sql = "INSERT INTO Publisher (PublisherName) VALUES (?)";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, publisher.getPublisherName());
            
            int affectedRows = st.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        publisher.setPublisherId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return publisher;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting publisher", e);
        }
        
        return null;
    }

    private Publisher update(Publisher publisher) {
        String sql = "UPDATE Publisher SET PublisherName = ? WHERE PublisherID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setString(1, publisher.getPublisherName());
            st.setInt(2, publisher.getPublisherId());
            
            st.executeUpdate();
            return publisher;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating publisher", e);
        }
        
        return null;
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM Publisher WHERE PublisherID = ?";
        
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting publisher with ID: " + id, e);
        }
    }
}
