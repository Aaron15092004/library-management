package com.group6.librarymanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Publisher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PublisherID")
    private Integer publisherId;

    @Column(name = "PublisherName", nullable = false, unique = true, length = 100)
    private String publisherName;
}
