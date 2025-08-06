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
    private RedisSessionService redisSessionService; // ✅ Inject session service

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            System.out.println("Starting authentication for: " + loginRequest.getEmail());

            // Lookup user
            Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail().toLowerCase());
            if (optionalUser.isEmpty()) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            User user = optionalUser.get();

            // Check password
            if (!encoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            System.out.println("Password verified");

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            System.out.println("JWT token generated successfully");

            // ✅ Extract session ID (jti) from token
            String sessionId = jwtUtil.getSessionIdFromToken(token);

            // ✅ Store session in Redis
            redisSessionService.storeSession(sessionId, user.getId(), user.getEmail());

            return new LoginResponse(token, "Login successful", user.getId(), user.getEmail());
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean validateSession(String token) {
        try {
            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                return false;
            }

            String sessionId = jwtUtil.getSessionIdFromToken(token);
            return redisSessionService.isSessionValid(sessionId); // ✅ Check in Redis
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
