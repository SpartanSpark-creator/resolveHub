package com.jsp.resolvehub.security;

import com.jsp.resolvehub.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService {

    private final String jwt_secret = "pY3X+fK0ZpZ1zK0nQeYFq2b7Kz6y5y0R7mGxR8cZx9Q=";
    private Long expriyTime = 360000l;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor( Base64.getDecoder().decode(jwt_secret));
    }

    public String generateToken(User user){
        Map<String , Object> claims=new HashMap<>();
        claims.put("role",user.getRoles());

        Date now=new Date();
        Date expiry=new Date(now.getTime()+expriyTime);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();

    }
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
    public String extractUserName(String token){
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRoles(String token){
        Claims claims= Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role").toString();
    }
}