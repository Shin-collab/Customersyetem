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

    @Column(name = "created_by")
    private String createdBy;

    private String name;        
    private String nickname;    
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String houseNo;     
    private String province;    
    private String zipcode;     

    private String occupation;  
    private String workPlace;   
    private String education;   
    private String major;       
    private String educationYear; 
    private String schoolName;  

    private String disease; 
    private String allergy; 

    // --- เพิ่ม Getter & Setter แบบ Manual เพื่อแก้ปัญหา Build Error ใน VS Code ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getHouseNo() { return houseNo; }
    public void setHouseNo(String houseNo) { this.houseNo = houseNo; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getWorkPlace() { return workPlace; }
    public void setWorkPlace(String workPlace) { this.workPlace = workPlace; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getEducationYear() { return educationYear; }
    public void setEducationYear(String educationYear) { this.educationYear = educationYear; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getAllergy() { return allergy; }
    public void setAllergy(String allergy) { this.allergy = allergy; }

    // Helper: คำนวณอายุ
    public int getAge() {
        return (birthDate != null) ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
    }
}