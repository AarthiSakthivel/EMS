package com.ems2p0.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ems2p0.model.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * EMS 2.0 - JWT functional component to manipulate the token by using secret
 * claims and expirations and extracting the user details from the token for the
 * authorization
 *
 * @author Mohan
 * @version v1.0.0
 * @category Security component
 * @apiNote - Developer should be responsible to handle all of the JWT token
 * operations and the changes will impact the token authorization
 * within the application.
 */
@Component
public class JwtUtils {

    @Value("${app.token.sign-in.key}")
    private String jwtSigningKey;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getEmployeeRoleManagement().getOfficialRole());
        return generateToken(claims, userDetails, expiration);
    }
    
    public String generateRefreshToken(UserDetails userDetails,Long refreshTokenExpiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getEmployeeRoleManagement().getOfficialRole());
        return generateToken(claims, userDetails, refreshTokenExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expiration) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
