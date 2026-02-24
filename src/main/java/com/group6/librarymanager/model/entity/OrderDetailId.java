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
public class OrderDetailId implements Serializable {

    @Column(name = "OrderID")
    private Integer orderId;

    @Column(name = "BookID")
    private Integer bookId;
}
