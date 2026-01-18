
package com.example.customersystem.controller;

import com.example.customersystem.model.User;
import com.example.customersystem.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    // แก้ไข Constructor ให้รับเฉพาะ UserService (เพราะเราย้ายเรื่องลูกค้าไปไว้ที่ MainController แล้ว)
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String viewRegisterPage() {
        return "register";
    }

    // --- ส่วนที่เคยเป็น @GetMapping("/") ถูกลบออกแล้ว เพื่อไม่ให้ทับซ้อนกับ MainController ---

    @PostMapping("/register")
    public String registerUser(User user) {
        userService.saveUser(user);
        return "redirect:/login"; 
    }

    @GetMapping("/change-password")
    public String viewChangePasswordPage() {
        return "change-password";
    }
}