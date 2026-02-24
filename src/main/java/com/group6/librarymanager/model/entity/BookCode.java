package com.group6.librarymanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BookCode")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookCodeID")
    private Integer bookCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID", nullable = false)
    private Book book;

    @Column(name = "BookCode", nullable = false, unique = true, length = 50)
    private String bookCode;
}
