package com.fistein.service.impl;

import com.fistein.dto.LoginRequest;
import com.fistein.dto.RegisterRequest;
import com.fistein.dto.JwtResponse;
import com.fistein.entity.User;
import com.fistein.repository.UserRepository;
import com.fistein.security.JwtUtil; // Bu import doğru olanı
import com.fistein.service.AuthService;
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
    private final AuthenticationManager authenticationManager; // main dalından eklendi
    private final UserDetailsService userDetailsService; // main dalından eklendi

    @Override
    public JwtResponse register(RegisterRequest request) {
        // Email kontrolü
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor"); // cursor dalından korundu
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // JWT token üretimi için UserDetails kullanılıyor (main dalındaki mantık)
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var jwtToken = jwtUtil.generateToken(userDetails); // JwtUtil'in UserDetails alan generateToken metodunu kullanır

        return new JwtResponse(jwtToken);
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
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı")); // cursor dalından korundu

        // Parola kontrolü AuthenticationManager tarafından zaten yapıldığı için burada tekrar etmiyoruz.
        // Eğer AuthenticationManager başarısız olursa, bir AuthenticationException fırlatır.

        // JWT token üretimi için UserDetails kullanılıyor (main dalındaki mantık)
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var jwtToken = jwtUtil.generateToken(userDetails); // JwtUtil'in UserDetails alan generateToken metodunu kullanır

        return new JwtResponse(jwtToken);
    }
}
