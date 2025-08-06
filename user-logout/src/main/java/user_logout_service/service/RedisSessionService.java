package user_logout_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "user_session:";

    public boolean isSessionValid(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
    }

    public String getUserIdFromSession(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        return (String) redisTemplate.opsForHash().get(sessionKey, "userId");
    }

    public void removeSession(String sessionId) {
        String sessionKey = SESSION_PREFIX + sessionId;
        redisTemplate.delete(sessionKey);
    }
}