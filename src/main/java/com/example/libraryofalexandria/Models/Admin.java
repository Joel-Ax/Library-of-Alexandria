package com.example.libraryofalexandria.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int failedAttempts = 0;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean accountLocked = false;

    @Column(name = "lock_time")
    private Long lockTime; // Tidpunkt när kontot låstes, för att hantera upplåsning efter en viss tid.
}

