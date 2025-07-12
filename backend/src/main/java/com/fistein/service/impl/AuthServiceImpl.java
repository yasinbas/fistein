package com.fistein.service.impl;

import com.fistein.dto.LoginRequest;
import com.fistein.dto.RegisterRequest;
import com.fistein.dto.JwtResponse;
import com.fistein.dto.UserResponse;
import com.fistein.entity.User;
import com.fistein.repository.UserRepository;
import com.fistein.util.JwtUtil;
import com.fistein.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

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
                .name(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Kullanıcıyı kaydettikten sonra geri al (id ve createdAt için)
        user = userRepository.save(user);

        // JWT token üretimi için UserDetails kullanılıyor
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var jwtToken = jwtUtil.generateToken(userDetails);

        // UserResponse oluşturma
        var userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getEmail()) // Email'i username olarak kullan
                .email(user.getEmail())
                .fullName(user.getName())
                .createdAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        return new JwtResponse(jwtToken, userResponse);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        // AuthenticationManager ile kimlik doğrulama
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // JWT token üretimi için UserDetails kullanılıyor
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var jwtToken = jwtUtil.generateToken(userDetails);

        // UserResponse oluşturma
        var userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getEmail()) // Email'i username olarak kullan
                .email(user.getEmail())
                .fullName(user.getName())
                .createdAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        return new JwtResponse(jwtToken, userResponse);
    }
}
