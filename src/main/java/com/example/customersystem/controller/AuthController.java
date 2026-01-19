package com.example.customersystem.controller;

import com.example.customersystem.model.User;
import com.example.customersystem.service.UserService;
import com.example.customersystem.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping; // เพิ่มอันนี้
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // ==========================================
    // 1. ระบบ LOGIN (แก้ Error 405 ที่นี่)
    // ==========================================
    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

    // ใช้ @RequestMapping เพื่อรองรับทั้ง GET และ POST จาก Spring Security
    @RequestMapping("/login-success")
    public String handleLoginSuccess(HttpSession session, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login?error";
            }
            
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) return "redirect:/login?error";
            
            String targetEmail = user.getEmail(); 
            
            // สร้าง OTP สำหรับ Login
            String otp = String.format("%06d", new Random().nextInt(999999));
            session.setAttribute("OTP_CODE", otp);
            session.setAttribute("PENDING_USER", user);
            
            // ส่ง Email OTP
            emailService.sendOtpEmail(targetEmail, otp);
            
            // ส่งไปหน้ายืนยัน OTP (หน้า verify-otp.html)
            return "redirect:/verify-otp";
            
        } catch (Exception e) {
            return "redirect:/login?error"; 
        }
    }

    // ==========================================
    // 2. ระบบ REGISTER
    // ==========================================
    @GetMapping("/register")
    public String viewRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            userService.saveUser(user);
            return "redirect:/login?registered=true"; 
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    // ==========================================
    // 3. ระบบยืนยัน OTP (หลังจาก Login สำเร็จ)
    // ==========================================
    @GetMapping("/verify-otp")
    public String viewVerifyOtpPage(HttpSession session) {
        if (session.getAttribute("OTP_CODE") == null) return "redirect:/login";
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, HttpSession session, Model model) {
        String sessionOtp = (String) session.getAttribute("OTP_CODE");
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            session.removeAttribute("OTP_CODE");
            return "redirect:/"; // เข้าสู่หน้าแรกของระบบ
        }
        model.addAttribute("error", "รหัส OTP ไม่ถูกต้อง");
        return "verify-otp";
    }

    // ==========================================
    // 4. ระบบลืมรหัสผ่าน (FORGOT PASSWORD)
    // ==========================================
    @GetMapping("/forgot-password")
    public String viewForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, Model model) {
        User user = userService.findByEmail(email);
        if (user != null) {
            String otp = String.format("%06d", new Random().nextInt(999999));
            session.setAttribute("FORGOT_PASS_OTP", otp);
            session.setAttribute("FORGOT_USER_EMAIL", email);
            
            emailService.sendOtpEmail(email, otp);
            return "redirect:/verify-forgot-password";
        }
        model.addAttribute("error", "ไม่พบอีเมลนี้ในระบบ");
        return "forgot-password";
    }

    @GetMapping("/verify-forgot-password")
    public String viewVerifyForgotOtpPage() {
        return "verify-forgot-password";
    }

    @PostMapping("/verify-forgot-password")
    public String verifyForgotOtp(@RequestParam String otp, HttpSession session, Model model) {
        String sessionOtp = (String) session.getAttribute("FORGOT_PASS_OTP");
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            return "redirect:/reset-password";
        }
        model.addAttribute("error", "รหัส OTP ไม่ถูกต้อง");
        return "verify-forgot-password";
    }

    @GetMapping("/reset-password")
    public String viewResetPasswordPage(HttpSession session) {
        if (session.getAttribute("FORGOT_USER_EMAIL") == null) return "redirect:/login";
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("FORGOT_USER_EMAIL");
        User user = userService.findByEmail(email);
        if (user != null) {
            user.setPassword(newPassword); 
            userService.saveUser(user); 
            session.invalidate(); 
            return "redirect:/login?passwordReset=true";
        }
        return "redirect:/login";
    }

    // ==========================================
    // 5. ระบบเปลี่ยนรหัสในหน้า PROFILE
    // ==========================================
    @PostMapping("/request-change-password")
    public String requestChangePassword(@RequestParam String newPassword, HttpSession session, Authentication authentication) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("CHANGE_PASS_OTP", otp);
        session.setAttribute("NEW_PASSWORD_TEMP", newPassword);
        User user = userService.findByUsername(authentication.getName());
        emailService.sendOtpEmail(user.getEmail(), otp);
        return "redirect:/verify-change-password";
    }

    @GetMapping("/verify-change-password")
    public String viewVerifyChangeOtpPage() {
        return "verify-change-password";
    }

    @PostMapping("/verify-change-password")
    public String verifyChangePassword(@RequestParam String otp, HttpSession session, Authentication authentication, Model model) {
        String sessionOtp = (String) session.getAttribute("CHANGE_PASS_OTP");
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            User user = userService.findByUsername(authentication.getName());
            user.setPassword((String) session.getAttribute("NEW_PASSWORD_TEMP"));
            userService.saveUser(user);
            session.removeAttribute("CHANGE_PASS_OTP");
            return "redirect:/login?passwordChanged=true";
        }
        model.addAttribute("error", "รหัส OTP ไม่ถูกต้อง");
        return "verify-change-password";
    }
}