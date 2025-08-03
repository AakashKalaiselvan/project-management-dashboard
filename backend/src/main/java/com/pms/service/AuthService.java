package com.pms.service;

import com.pms.dto.AuthRequest;
import com.pms.dto.AuthResponse;
import com.pms.entity.User;
import com.pms.repository.UserRepository;
import com.pms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER); // Default role

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(user, user.getRole().name());
        return new AuthResponse(token, savedUser.getEmail(), savedUser.getName(), savedUser.getRole().name(), savedUser.getId());
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user, user.getRole().name());
            return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name(), user.getId());
        } catch (Exception e) {
            return new AuthResponse("Invalid email or password");
        }
    }
} 