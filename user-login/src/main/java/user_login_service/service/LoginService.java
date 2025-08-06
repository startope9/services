package user_login_service.service;

import user_login_service.dto.LoginRequest;
import user_login_service.dto.LoginResponse;
import user_login_service.model.User;
import user_login_service.repository.UserRepository;
import user_login_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    // MongoDB repository is disabled temporarily for testing
    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Redis session service is disabled temporarily for testing
    // @Autowired
    // private RedisSessionService redisSessionService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public LoginResponse authenticate(LoginRequest loginRequest) {
        // Temporary: Create a test user bypassing MongoDB and Redis
        try {
            System.out.println("Starting authentication for: " + loginRequest.getEmail());
            
            // Skip MongoDB lookup and create a test user
            User testUser = new User();
            testUser.setId("test-user-id");
            testUser.setEmail(loginRequest.getEmail().toLowerCase());
            testUser.setPasswordHash(encoder.encode("TestPass123"));
            
            System.out.println("Test user created");

            // Verify password (for test user, accept TestPass123)
            if (!"TestPass123".equals(loginRequest.getPassword())) {
                throw new IllegalArgumentException("Invalid email or password");
            }
            
            System.out.println("Password verified");

            // Generate JWT token - this is where the error likely occurs
            String token = jwtUtil.generateToken(testUser.getId(), testUser.getEmail());
            System.out.println("JWT token generated successfully");
            
            // Skip Redis storage to isolate JWT issue
            System.out.println("Skipping Redis storage for testing");

            return new LoginResponse(token, "Login successful", testUser.getId(), testUser.getEmail());
        } catch (Exception e) {
            System.err.println("Authentication error details: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean validateSession(String token) {
        try {
            // First validate JWT token structure and signature
            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                return false;
            }

            // Skip Redis check for testing
            // String sessionId = jwtUtil.getSessionIdFromToken(token);
            // boolean isValid = redisSessionService.isSessionValid(sessionId);
            // 
            // // Extend session if valid
            // if (isValid) {
            //     redisSessionService.extendSession(sessionId);
            // }
            // 
            // return isValid;
            
            return true; // For testing, just return true if JWT is valid
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
