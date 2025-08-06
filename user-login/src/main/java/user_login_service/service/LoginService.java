package user_login_service.service;

import user_login_service.dto.LoginRequest;
import user_login_service.dto.LoginResponse;
import user_login_service.model.User;
import user_login_service.repository.UserRepository;
import user_login_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisSessionService redisSessionService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public LoginResponse authenticate(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail().toLowerCase());
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOptional.get();

        // Verify password
        if (!encoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        // Extract session ID from token and store in Redis
        String sessionId = jwtUtil.getSessionIdFromToken(token);
        redisSessionService.storeSession(sessionId, user.getId(), user.getEmail());

        return new LoginResponse(token, "Login successful", user.getId(), user.getEmail());
    }

    public boolean validateSession(String token) {
        try {
            // First validate JWT token structure and signature
            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                return false;
            }

            // Then check if session exists in Redis
            String sessionId = jwtUtil.getSessionIdFromToken(token);
            boolean isValid = redisSessionService.isSessionValid(sessionId);
            
            // Extend session if valid
            if (isValid) {
                redisSessionService.extendSession(sessionId);
            }
            
            return isValid;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.getEmailFromToken(token);
    }
}