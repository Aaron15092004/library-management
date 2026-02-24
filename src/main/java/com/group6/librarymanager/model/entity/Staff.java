package com.group6.librarymanager.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StaffID")
    private Integer staffId;

    @Column(name = "StaffName", nullable = false, length = 100)
    private String staffName;

    @ManyToMany
    @JoinTable(name = "StaffRole", joinColumns = @JoinColumn(name = "StaffID"), inverseJoinColumns = @JoinColumn(name = "RoleID"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
