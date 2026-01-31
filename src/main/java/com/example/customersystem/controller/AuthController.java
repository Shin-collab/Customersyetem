package com.example.customersystem.controller;

import com.example.customersystem.model.User;
import com.example.customersystem.service.UserService;
import com.example.customersystem.service.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String handleLoginSuccess(HttpSession session, Authentication authentication,
                                   @CookieValue(value = "trusted_device", defaultValue = "false") String isTrusted) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("❌ [Auth] Authentication failed or null");
                return "redirect:/login?error";
            }
            
            if ("true".equals(isTrusted)) {
                System.out.println("✅ [Auth] Trusted device found, skipping OTP");
                return "redirect:/"; 
            }

            String username = authentication.getName();
            System.out.println("📩 [Auth] Login success for user: " + username);
            
            User user = userService.findByUsername(username);
            
            if (user == null) {
                System.out.println("❌ [Auth] User not found in DB for username: " + username);
                return "redirect:/login?error";
            }
            
            String targetEmail = user.getEmail(); 
            String otp = String.format("%06d", new Random().nextInt(1000000));
            
            System.out.println("🚀 [Auth] Generating OTP for email: " + targetEmail);
            
            session.setAttribute("OTP_CODE", otp);
            session.setAttribute("PENDING_USER", user);
            
            // ✅ เรียกส่งเมล
            emailService.sendOtpEmail(targetEmail, otp);
            
            return "redirect:/verify-otp";
            
        } catch (Exception e) {
            System.err.println("❌ [Auth] Error in handleLoginSuccess: " + e.getMessage());
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
    public String verifyOtp(@RequestParam String otp, HttpSession session, HttpServletResponse response, Model model) {
        String sessionOtp = (String) session.getAttribute("OTP_CODE");
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            session.removeAttribute("OTP_CODE");

            Cookie cookie = new Cookie("trusted_device", "true");
            cookie.setMaxAge(7 * 24 * 60 * 60); 
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return "redirect:/";
        }
        model.addAttribute("error", "รหัส OTP ไม่ถูกต้อง");
        return "verify-otp";
    }

    @GetMapping("/forgot-password")
    public String viewForgotPasswordPage() { return "forgot-password"; }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, Model model) {
        System.out.println("📩 [ForgotPass] Request for email: " + email);
        User user = userService.findByEmail(email);
        if (user != null) {
            String otp = String.format("%06d", new Random().nextInt(1000000));
            session.setAttribute("FORGOT_PASS_OTP", otp);
            session.setAttribute("FORGOT_USER_EMAIL", email);
            
            System.out.println("🚀 [ForgotPass] Sending OTP to: " + email);
            emailService.sendOtpEmail(email, otp);
            
            return "redirect:/verify-forgot-password";
        }
        System.out.println("❌ [ForgotPass] Email not found: " + email);
        model.addAttribute("error", "ไม่พบอีเมลนี้ในระบบ");
        return "forgot-password";
    }

    @GetMapping("/verify-forgot-password")
    public String viewVerifyForgotOtpPage() { return "verify-forgot-password"; }

    @PostMapping("/verify-forgot-password")
    public String verifyForgotOtp(@RequestParam String otp, HttpSession session, Model model) {
        String sessionOtp = (String) session.getAttribute("FORGOT_PASS_OTP");
        if (sessionOtp != null && sessionOtp.equals(otp)) { return "redirect:/reset-password"; }
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
        System.out.println("🚀 [ChangePass] Sending OTP to: " + user.getEmail());
        emailService.sendOtpEmail(user.getEmail(), otp);
        return "redirect:/verify-change-password";
    }

    @GetMapping("/verify-change-password")
    public String viewVerifyChangeOtpPage() { return "verify-change-password"; }

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