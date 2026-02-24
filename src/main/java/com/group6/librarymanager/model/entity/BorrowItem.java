package com.group6.librarymanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BorrowItem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowItem {

    @EmbeddedId
    private BorrowItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("borrowId")
    @JoinColumn(name = "BorrowID")
    private Borrow borrow;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "BookID")
    private Book book;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;
}
