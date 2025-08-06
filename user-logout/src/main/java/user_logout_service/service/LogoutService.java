package user_logout_service.service;

import user_logout_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisSessionService redisSessionService;

    public boolean logout(String token) {
        try {
            // Validate token structure first
            if (!jwtUtil.validateToken(token)) {
                return false;
            }

            // Extract session ID from token
            String sessionId = jwtUtil.getSessionIdFromToken(token);
            
            // Check if session exists in Redis
            if (!redisSessionService.isSessionValid(sessionId)) {
                return false; // Session already doesn't exist
            }

            // Remove session from Redis
            redisSessionService.removeSession(sessionId);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSessionValid(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return false;
            }

            String sessionId = jwtUtil.getSessionIdFromToken(token);
            return redisSessionService.isSessionValid(sessionId);
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}