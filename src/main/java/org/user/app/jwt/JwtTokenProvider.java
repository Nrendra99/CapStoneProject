package org.user.app.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.Key;

@Service
public class JwtTokenProvider {

	   @Value("${app.jwt-secret}")
	    private String jwtSecret;



    // Generate JWT token with username as the subject
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
        		.claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 12 * 3600000)) // 12 hours
                .and()
                .signWith(key())
                .compact();
    }
    

    // get username from JWT token
    public String getUsername(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
  
    // validate JWT token
    public boolean validateToken(String token){
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;

    }



    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}