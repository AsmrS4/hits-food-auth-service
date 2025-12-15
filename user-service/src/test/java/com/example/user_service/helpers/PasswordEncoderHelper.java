package com.example.user_service.helpers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderHelper {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static boolean passwordsAreMatch(String requestPassword, String encodedPassword) {
        return passwordEncoder.matches(requestPassword, encodedPassword);
    }
}
