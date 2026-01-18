package com.example.customersystem.service;

import com.example.customersystem.model.User;
import com.example.customersystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginInput) throws UsernameNotFoundException {
        // ค้นหาจาก Username ก่อน ถ้าไม่เจอค่อยหาจาก Email (ทำให้ระบบยืดหยุ่นมาก)
        User user = userRepository.findByUsername(loginInput);
        if (user == null) {
            user = userRepository.findByEmail(loginInput);
        }

        if (user == null) {
            throw new UsernameNotFoundException("ไม่พบผู้ใช้งานหรืออีเมล: " + loginInput);
        }

        // คืนค่า UserDetails พร้อมสิทธิ์ว่าง (ArrayList) ไปก่อนเพื่อความประหยัดบรรทัด
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), // ใช้ Username เป็นหลักในการระบุตัวตนระบบ
            user.getPassword(), 
            new ArrayList<>()
        );
    }
}