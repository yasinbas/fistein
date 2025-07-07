package com.fistein.service.impl;

import com.fistein.dto.LoginRequest;
import com.fistein.dto.RegisterRequest;
import com.fistein.dto.JwtResponse;
import com.fistein.entity.User;
import com.fistein.repository.UserRepository;
import com.fistein.service.AuthService;
import com.fistein.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

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

        // JWT token üret
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var jwtToken = jwtUtil.generateToken(userDetails);

        return new JwtResponse(jwtToken);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        // Authentication kontrolü
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // JWT token üret
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var jwtToken = jwtUtil.generateToken(userDetails);

        return new JwtResponse(jwtToken);
    }
}
