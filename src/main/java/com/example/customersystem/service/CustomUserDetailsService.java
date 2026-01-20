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
        User user = userRepository.findByUsername(loginInput);
        if (user == null) {
            user = userRepository.findByEmail(loginInput);
        }

        if (user == null) {
            throw new UsernameNotFoundException("ไม่พบผู้ใช้งานหรืออีเมล: " + loginInput);
        }
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), 
            user.getPassword(), 
            new ArrayList<>()
        );
    }
}