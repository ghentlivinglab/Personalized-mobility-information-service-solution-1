package be.ugent.vopro5.backend.businesslayer.util;

import io.jsonwebtoken.*;

/**
 * Created by thibault on 4/3/16.
 */
public class RefreshToken {
    private final String subject;

    /**
     * @param subject The subject of the token. This is the identifier of the person who obtained the refresh-token.
     */
    public RefreshToken(String subject) {
        this.subject = subject;
    }

    /**
     * @return
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Hash this refresh-token.
     * @param secret
     * @return
     */
    public String toToken(String secret) {
        return Jwts.builder().setSubject(this.subject).signWith(SignatureAlgorithm.HS512, secret).compact(); // SHA 512
    }

    /**
     * Decrypt the refresh-token.
     * @param secret
     * @param token The hashed refresh-token.
     * @return
     */
    public static RefreshToken parseToken(String secret, String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            String userId = claims.getBody().getSubject();
            return new RefreshToken(userId);
        } catch (SignatureException | IllegalArgumentException | MalformedJwtException e) {
            // JWT is invalid
            return null;
        }
    }
}
