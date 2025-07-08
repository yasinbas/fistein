package com.fistein.service;

import com.fistein.dto.GoogleLoginRequest;
import com.fistein.dto.JwtResponse;

public interface GoogleOAuthService {
    JwtResponse authenticateWithGoogle(GoogleLoginRequest request);
}