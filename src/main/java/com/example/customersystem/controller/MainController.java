package com.example.customersystem.controller;

import com.example.customersystem.model.Customer;
import com.example.customersystem.repository.CustomerRepository;
import com.example.customersystem.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.List;

@Controller
public class MainController {

    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;

    public MainController(CustomerRepository customerRepo, UserRepository userRepo) {
        this.customerRepo = customerRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            // 1. ดึง Username ของคนที่ล็อกอินอยู่ (ตัวเดิมที่พี่ใช้ดึงโปรไฟล์)
            String username = principal.getName();
            
            // 2. ดึงเฉพาะลูกค้าที่ User คนนี้เป็นคนสร้าง
            List<Customer> customers = customerRepo.findByCreatedBy(username);
            model.addAttribute("customers", customers);

            // 3. ดึงข้อมูล User สำหรับโชว์ชื่อ/รูปโปรไฟล์
            model.addAttribute("user", userRepo.findByUsername(username));
        }
        return "index";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            
            model.addAttribute("user", userRepo.findByUsername(username));
            
            // โชว์เฉพาะข้อมูลของตัวเองในหน้า Profile
            model.addAttribute("customers", customerRepo.findByCreatedBy(username));
        }
        return "profile";
    }
}