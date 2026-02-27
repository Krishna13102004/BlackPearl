package com.blackpearl.controller;

import com.blackpearl.model.User;
import com.blackpearl.repository.UserRepository;
import com.blackpearl.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"}, allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.get("email"), req.get("password")));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.get("email"));
        User user = userRepository.findByEmail(req.get("email")).orElseThrow();

        if (!user.isActive()) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "User account is inactive. Please contact admin."));
        }

        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "name", user.getFullName(),
                        "email", user.getEmail(),
                        "role", user.getRole().name(),
                        "department", user.getDepartment() != null ? user.getDepartment().name() : "")));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        if (userRepository.countByEmail(req.get("email")) > 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }

        User.Role role = User.Role.USER;
        if ("ADMIN".equalsIgnoreCase(req.get("role"))) {
            role = User.Role.ADMIN;
        }

        User.Department dept = User.Department.OTHER;
        try {
            if (req.get("department") != null) {
                dept = User.Department.valueOf(req.get("department").toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            // Default to OTHER if invalid department provided
        }

        User user = User.builder()
                .firstName(req.get("firstName"))
                .lastName(req.get("lastName"))
                .email(req.get("email"))
                .phone(req.get("phone"))
                .department(dept)
                .password(passwordEncoder.encode(req.get("password")))
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getFullName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "department", user.getDepartment() != null ? user.getDepartment().name() : ""));
    }
}
