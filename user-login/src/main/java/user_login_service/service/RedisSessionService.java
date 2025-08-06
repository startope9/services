package user_login_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RedisSessionService {

    private static final String SESSION_PREFIX = "user_session:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void storeSession(String sessionId, String userId, String email) {
        String key = SESSION_PREFIX + sessionId;

        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("userId", String.valueOf(userId));   // 🔥 ensure String
        sessionData.put("email", String.valueOf(email));     // 🔥 ensure String

        redisTemplate.opsForHash().putAll(key, sessionData);
        redisTemplate.expire(key, Duration.ofHours(24)); // Set expiration
    }

    public boolean isSessionValid(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }
}
