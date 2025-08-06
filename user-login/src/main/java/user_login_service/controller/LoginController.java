package user_login_service.controller;

import user_login_service.dto.LoginRequest;
import user_login_service.dto.LoginResponse;
import user_login_service.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = loginService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // Log the actual exception for debugging
            System.err.println("Login error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal server error: " + e.getClass().getSimpleName() + " - " + e.getMessage()));

        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("valid", false));
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            boolean isValid = loginService.validateSession(token);

            if (isValid) {
                String userId = loginService.getUserIdFromToken(token);
                String email = loginService.getEmailFromToken(token);
                
                Map<String, Object> response = Map.of(
                    "valid", true,
                    "userId", userId,
                    "email", email
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("valid", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("valid", false));
        }
    }
}