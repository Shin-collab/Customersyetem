package com.example.customersystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; 

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    private String name; 
    private String avatar; 
    private String resetPasswordToken; 
}