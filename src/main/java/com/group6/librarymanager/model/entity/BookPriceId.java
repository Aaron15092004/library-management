package com.group6.librarymanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookPriceId implements Serializable {

    @Column(name = "BookID")
    private Integer bookId;

    @Column(name = "PriceID")
    private Integer priceId;

    @Column(name = "StartDate")
    private LocalDate startDate;
}
