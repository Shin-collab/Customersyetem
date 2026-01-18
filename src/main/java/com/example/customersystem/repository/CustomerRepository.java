package com.example.customersystem.repository;

import com.example.customersystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // เพิ่มบรรทัดนี้ครับ: เพื่อให้หาข้อมูลเฉพาะลูกค้าที่ User คนนั้นๆ เป็นคนสร้าง
    List<Customer> findByCreatedBy(String email);
    
}