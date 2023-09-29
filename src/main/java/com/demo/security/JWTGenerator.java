package com.demo.security;

import java.security.Key;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTGenerator {
	private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	
	public String generateToken(Authentication authentication) {
		String username=authentication.getName();
		Date currentDate=new Date();
		Date expireDate=new Date(currentDate.getTime()+SecurityConstant.JWTEXPIRATION);
		
		String token=Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(expireDate)
				.signWith(key,SignatureAlgorithm.HS512)
				.compact();
		return token;
	}
	
	public String getusernameFromJWT(String token) {
		Claims claims=Jwts.parserBuilder().setSigningKey(key)
				.build().parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}
	
	public boolean validateToken(String Token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(Token);
			return true;
			
		}catch (Exception e) {
			throw new AuthenticationCredentialsNotFoundException("Jwt was expired or incorrect");
		}
	}

}
