package user_logout_service.controller;

import user_logout_service.service.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LogoutController {

    @Autowired
    private LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid authorization header"));
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // Get user ID before logout for logging purposes
            String userId = logoutService.getUserIdFromToken(token);
            
            boolean loggedOut = logoutService.logout(token);

            if (loggedOut) {
                Map<String, Object> response = Map.of(
                    "message", "Logout successful",
                    "success", true,
                    "userId", userId != null ? userId : "unknown"
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Session not found or already expired"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Internal server error"));
        }
    }

    @GetMapping("/session/status")
    public ResponseEntity<Map<String, Object>> getSessionStatus(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("valid", false, "error", "Invalid authorization header"));
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            boolean isValid = logoutService.isSessionValid(token);
            
            if (isValid) {
                String userId = logoutService.getUserIdFromToken(token);
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "userId", userId != null ? userId : "unknown"
                ));
            } else {
                return ResponseEntity.ok(Map.of("valid", false));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false, "error", "Token validation failed"));
        }
    }
}