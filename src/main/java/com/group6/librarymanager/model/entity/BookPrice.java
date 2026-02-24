package com.group6.librarymanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "BookPrice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookPrice {

    @EmbeddedId
    private BookPriceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "BookID")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("priceId")
    @JoinColumn(name = "PriceID")
    private Price price;

    @Column(name = "EndDate")
    private LocalDate endDate;
}
