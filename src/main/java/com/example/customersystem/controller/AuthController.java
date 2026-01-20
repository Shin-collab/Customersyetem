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
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

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
            String otp = String.format("%06d", new Random().nextInt(1000000));
            
            session.setAttribute("OTP_CODE", otp);
            session.setAttribute("PENDING_USER", user);
            
            // ส่ง Email ผ่าน Service (ต้องมั่นใจว่าใน EmailService ชื่อ method นี้รับ String, String)
            emailService.sendOtpEmail(targetEmail, otp);
            
            return "redirect:/verify-otp";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?error"; 
        }
    }

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
            return "redirect:/";
        }
        model.addAttribute("error", "รหัส OTP ไม่ถูกต้อง");
        return "verify-otp";
    }

    @GetMapping("/forgot-password")
    public String viewForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, Model model) {
        User user = userService.findByEmail(email);
        if (user != null) {
            String otp = String.format("%06d", new Random().nextInt(1000000));
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

    @PostMapping("/request-change-password")
    public String requestChangePassword(@RequestParam String newPassword, HttpSession session, Authentication authentication) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
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