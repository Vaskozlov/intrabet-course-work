package org.vaskozlov.is.course.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtTokenService implements TokenService {
    // TODO: take from ENV
    private static final String JWT_SECRET = "5d2f6d1f8065fbedbbc0a0ec047704ddc8054c3c42a92fc7b3c2cbc68df2a868";

    private static final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(3);

    @Override
    public String getTokenType() {
        return "Bearer";
    }

    @Override
    public String createToken(UserDetails userDetails) {
        var issueTimeMs = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(Collections.emptyMap())
                .issuedAt(new Date(issueTimeMs))
                .expiration(new Date(issueTimeMs + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }

    @Override
    public String getUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(
            @NonNull String token,
            @NonNull UserDetails userDetails
    ) {
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String createToken(String subject, Map<String, Object> claims) {
        long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(3);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
