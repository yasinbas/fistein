package com.fistein.service.impl;

import com.fistein.dto.GoogleLoginRequest;
import com.fistein.dto.JwtResponse;
import com.fistein.dto.UserResponse;
import com.fistein.entity.User;
import com.fistein.repository.UserRepository;
import com.fistein.service.GoogleOAuthService;
import com.fistein.util.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Value("${google.oauth.client-id:your-google-client-id}")
    private String googleClientId;

    @Override
    public JwtResponse authenticateWithGoogle(GoogleLoginRequest request) {
        try {
            // Verify the Google ID token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String googleId = payload.getSubject();

            // Check if user exists, if not create a new user
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createNewGoogleUser(email, name, googleId));

            // Generate JWT token
            var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            var jwtToken = jwtUtil.generateToken(userDetails);

            // Create UserResponse
            var userResponse = UserResponse.builder()
                    .id(user.getId())
                    .username(user.getEmail())
                    .email(user.getEmail())
                    .fullName(user.getName())
                    .createdAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();

            return new JwtResponse(jwtToken, userResponse);

        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }

    private User createNewGoogleUser(String email, String name, String googleId) {
        var user = User.builder()
                .email(email)
                .name(name != null ? name : email) // Use email as name if name is not provided
                .password("") // No password for Google users
                .build();

        return userRepository.save(user);
    }
}