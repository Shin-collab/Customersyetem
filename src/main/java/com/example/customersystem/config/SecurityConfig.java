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
                // ปล่อยผ่านไฟล์ Static และหน้า Login/Register
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/img/**").permitAll()
                // หน้าอื่นๆ ทั้งหมด (หน้าแรก, เพิ่มลูกค้า, โปรไฟล์) ต้องล็อคอินเท่านั้น
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true) // ล็อคอินเสร็จไปหน้าแรก
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true) // เคลียร์ Session ให้สะอาด
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // ปิดเพื่อความง่ายในการส่งฟอร์ม (ในระดับโปรเจกต์เรียนรู้)
            
        return http.build();
    }
}