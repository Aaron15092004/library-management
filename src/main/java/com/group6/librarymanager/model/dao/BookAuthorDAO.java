package com.group6.librarymanager.model.dao;

/**
 * BookAuthor is a join table managed through Book.authors (ManyToMany).
 * Use BookDAO to query books by author.
 */
public interface BookAuthorDAO {
    // No methods needed — BookAuthor is managed via Book.authors relationship.
}
