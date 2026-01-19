package com.example.customersystem.config;

import com.example.customersystem.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 🛑 อนุญาตให้เข้าถึงหน้า Login, Register และระบบกู้รหัสผ่านทั้งหมดโดยไม่ต้อง Login
                .requestMatchers(
                    "/login", 
                    "/register", 
                    "/verify-otp", 
                    "/forgot-password", 
                    "/verify-forgot-password", 
                    "/reset-password",
                    "/css/**", 
                    "/js/**", 
                    "/img/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                // ✅ ใช้ defaultSuccessUrl เพื่อให้ส่งต่อไปที่ Controller หลัง Login สำเร็จเพื่อทำ OTP
                .defaultSuccessUrl("/login-success", true) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // ปิด CSRF เพื่อความสะดวกในการพัฒนาระบบหลังบ้าน
            
        return http.build();
    }
}