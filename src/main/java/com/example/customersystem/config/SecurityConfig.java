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
            .csrf(csrf -> csrf.disable()) // ปิดเพื่อให้ส่ง Form POST ได้ไม่ติดขัด
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login", 
                    "/register", 
                    "/forgot-password", 
                    "/verify-otp", 
                    "/verify-forgot-password", 
                    "/reset-password",
                    "/css/**", "/js/**", "/img/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/login-success", true) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
            
        return http.build();
    }
}