package com.group6.librarymanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BorrowItemId implements Serializable {

    @Column(name = "BorrowID")
    private Integer borrowId;

    @Column(name = "BookID")
    private Integer bookId;
}
