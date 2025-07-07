package com.fistein.service;

import com.fistein.dto.LoginRequest;
import com.fistein.dto.RegisterRequest;
import com.fistein.dto.JwtResponse;

public interface AuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
}
