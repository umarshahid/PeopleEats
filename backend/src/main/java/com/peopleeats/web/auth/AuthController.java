package com.peopleeats.web.auth;

import com.peopleeats.web.user.AuthResponse;
import com.peopleeats.web.user.LoginRequest;
import com.peopleeats.web.user.SignupRequest;
import com.peopleeats.web.user.UserRecord;
import com.peopleeats.web.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        String username = safeTrim(request.getUsername());
        String password = safeTrim(request.getPassword());
        String role = safeTrim(request.getRole()).toLowerCase(Locale.ROOT);

        if (username.isBlank() || password.isBlank() || role.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Username, password, and role are required.", null));
        }

        if (username.length() < 3) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Username must be at least 3 characters.", null));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Password must be at least 6 characters.", null));
        }

        if (!isAllowedRole(role)) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid role.", null));
        }

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(false, "Username already exists.", null));
        }

        String hashedPassword = passwordEncoder.encode(password);
        int inserted = userRepository.insertUser(username, hashedPassword, role);
        if (inserted > 0) {
            return ResponseEntity.ok(new AuthResponse(true, "Sign up successful.", role));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Sign up failed.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String username = safeTrim(request.getUsername());
        String password = safeTrim(request.getPassword());

        if (username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Username and password are required.", null));
        }

        Optional<UserRecord> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            UserRecord record = user.get();
            String stored = record.getPassword();
            boolean matches = passwordEncoder.matches(password, stored);
            if (!matches && password.equals(stored)) {
                String upgraded = passwordEncoder.encode(password);
                userRepository.updatePassword(record.getId(), upgraded);
                matches = true;
            }
            if (matches) {
                return ResponseEntity.ok(new AuthResponse(true, "Login successful.", record.getRole()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(false, "Invalid username or password.", null));
    }

    private boolean isAllowedRole(String role) {
        return role.equals("customer") || role.equals("restaurant") || role.equals("rider");
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
