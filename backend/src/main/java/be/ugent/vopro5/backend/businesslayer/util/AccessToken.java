package be.ugent.vopro5.backend.businesslayer.util;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import io.jsonwebtoken.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by thibault on 4/3/16.
 */
public class AccessToken {
    private final Date exp;
    private final String subject;

    /**
     * @param subject The subject of the token. This is the identifier of the person who obtained the access-token.
     * @param exp The date when the access-token will expire and thus will need to be refreshed.
     */
    public AccessToken(String subject, Date exp) {
        this.subject = subject;
        this.exp = exp;
    }

    private AccessToken(String subject) {
        this.exp = null;
        this.subject = subject;
    }

    /**
     * @return
     */
    public Date getExp() {
        return exp;
    }

    /**
     * @return
     */
    public String getSubject() {
        return subject;
    }


    /**
     * Hash this access-token.
     * @param secret
     * @return
     */
    public String toToken(String secret) {
        return Jwts.builder().setSubject(this.subject).setExpiration(exp).signWith(SignatureAlgorithm.HS512, secret).compact(); // SHA 512
    }

    /**
     * Decrypt the access-token.
     * @param secret
     * @param token The hashed access-token.
     * @return
     */
    public static AccessToken parseToken(String secret, String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            String userId = claims.getBody().getSubject();
            return new AccessToken(userId);
        } catch (SignatureException | MalformedJwtException e) {
            // JWT is invalid
            return null;
        }
    }
}
