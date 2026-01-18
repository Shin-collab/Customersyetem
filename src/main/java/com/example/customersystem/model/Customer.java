package com.example.customersystem.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "customers")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- System Security (เพิ่มใหม่เพื่อกั้นข้อมูล) ---
    @Column(name = "created_by")
    private String createdBy; // เก็บ Email ของคนสร้างข้อมูล (Gmail ที่ใช้ล็อกอิน)

    // --- Personal Info & Identity ---
    private String name;        
    private String nickname;    
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // --- Detailed Address ---
    private String houseNo;     
    private String province;    
    private String zipcode;     

    // --- Career & Education ---
    private String occupation;  
    private String workPlace;   
    private String education;   
    private String major;       
    private String educationYear; 
    private String schoolName;  

    // --- Health Records ---
    private String disease; 
    private String allergy; 

    // Helper: คำนวณอายุอัตโนมัติ
    public int getAge() {
        return (birthDate != null) ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
    }
}