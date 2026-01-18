package com.example.customersystem.controller;

import com.example.customersystem.model.Customer;
import com.example.customersystem.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepo;

    public CustomerController(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    @GetMapping("/add")
    public String viewAddPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "add-customer";
    }

    @GetMapping("/edit/{id}")
    public String viewEditPage(@PathVariable("id") Long id, Model model, Principal principal) {
        // เช็คความปลอดภัยเบื้องต้น
        if (id == null || principal == null) return "redirect:/";

        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer == null) return "redirect:/";

        // ตรวจสอบสิทธิ์: ชื่อคนล็อกอิน (principal.getName()) ตรงกับเจ้าของข้อมูลไหม
        String currentUsername = principal.getName();
        if (customer.getCreatedBy() == null || !customer.getCreatedBy().equals(currentUsername)) {
            return "redirect:/?error=no_permission"; 
        }

        model.addAttribute("customer", customer);
        return "add-customer";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id, Principal principal) {
        if (id == null || principal == null) return "redirect:/";

        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer != null) {
            String currentUsername = principal.getName();
            // ลบได้เฉพาะข้อมูลที่ตัวเองสร้างเท่านั้น
            if (currentUsername.equals(customer.getCreatedBy())) {
                customerRepo.deleteById(id);
            }
        }
        return "redirect:/";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("customer") Customer customer, Principal principal) {
        if (principal == null) return "redirect:/login";

        // ฝังชื่อ User ที่ล็อกอินอยู่ลงไปในฟิลด์ createdBy
        customer.setCreatedBy(principal.getName());

        // Logic จัดการข้อมูลการศึกษาเหมือนเดิม
        if (!"กำลังศึกษา".equals(customer.getOccupation())) {
            customer.setEducation("-");
            customer.setEducationYear("-");
            customer.setMajor("-");
            customer.setSchoolName("-");
        }
        
        customerRepo.save(customer);
        return "redirect:/"; 
    }
}