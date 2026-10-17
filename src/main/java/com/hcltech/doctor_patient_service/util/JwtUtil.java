package com.hcltech.doctor_patient_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret}")
    public String SECRET_KEY;

    public String extractUsernameFromToken(String token) {
        try {
            return extractClaimFromToken(token, Claims::getSubject);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            logger.error("JWT token error while extracting username: {}", e.getMessage());
            return null;
        }
    }

    public Date extractExpirationFromToken(String token) {
        return extractClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.error("JWT token has expired: {}", e.getMessage());
            throw e; // Re-throw the exception to explicitly indicate expiration
        } catch (MalformedJwtException e) {
            logger.error("JWT token is malformed: {}", e.getMessage());
            throw e; // Re-throw the exception to explicitly indicate malformed token
        } catch (SignatureException e) {
            logger.error("JWT signature is invalid: {}", e.getMessage());
            throw e; // Re-throw the exception to explicitly indicate invalid signature
        } catch (IllegalArgumentException e) {
            logger.error("JWT token is invalid (empty or null): {}", e.getMessage());
            throw e; // Re-throw for other invalid token issues
        }
    }

    public SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpirationFromToken(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .claims(claims)
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
