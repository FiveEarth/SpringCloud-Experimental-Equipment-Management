package edu.graduation.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Simple JWT helper for all services.
 */
public class JwtUtils {

    // TODO move to external config if needed
    private static final String SECRET_KEY =
            "c2ltcGxlLWxhYi1tYW5hZ2VtZW50LXBsYXRmb3JtLXNlY3JldC1rZXktMTIzNA==";

    private static final long DEFAULT_EXPIRE_MS = 24 * 60 * 60 * 1000L;

    private static Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expireAt = new Date(now + DEFAULT_EXPIRE_MS);
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expireAt)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }

    public static Claims getClaims(String token) {
        return parseToken(token).getBody();
    }

    public static boolean isExpired(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.before(new Date());
        } catch (Exception ex) {
            return true;
        }
    }
}

