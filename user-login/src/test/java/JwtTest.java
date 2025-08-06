import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtTest {

    @Test
    public void testJwtGeneration() {
        String jwtSecret = "mySecretKeyForJWTTokensThisShouldBeChangeInProduction123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        try {
            System.out.println("Secret key length: " + jwtSecret.getBytes().length + " bytes");
            
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            System.out.println("SecretKey created successfully");
            
            String token = Jwts.builder()
                .setSubject("test-user-id")
                .claim("email", "test@example.com")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
            
            System.out.println("Token generated successfully: " + token.substring(0, Math.min(50, token.length())) + "...");
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            System.out.println("Token validated successfully!");
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Email: " + claims.get("email"));
            
        } catch (Exception e) {
            System.err.println("JWT Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}