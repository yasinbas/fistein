package com.fistein.service.impl;

import com.fistein.dto.LoginRequest;
import com.fistein.dto.RegisterRequest;
import com.fistein.dto.JwtResponse;
import com.fistein.entity.User;
import com.fistein.repository.UserRepository;
import com.fistein.security.JwtUtil;
import com.fistein.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public JwtResponse register(RegisterRequest request) {
        // Email kontrolü
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor");
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Gerçek JWT token üretimi
        String token = jwtUtil.generateToken(user.getEmail());
        return new JwtResponse(token);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Geçersiz kimlik bilgileri");
        }

        // Gerçek JWT token üretimi
        String token = jwtUtil.generateToken(user.getEmail());
        return new JwtResponse(token);
    }
}
