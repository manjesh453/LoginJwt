package com.demo.security;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTGenerator {
	
	public String generateToken(Authentication authentication) {
		String username=authentication.getName();
		Date currentDate=new Date();
		Date expireDate=new Date(currentDate.getTime()+SecurityConstant.JWTEXPIRATION);
		
		String token=Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SECRETE)
				.compact();
		return token;
	}
	
	public String getusernameFromJWT(String token) {
		Claims claims=Jwts.parser().setSigningKey(SecurityConstant.JWT_SECRETE)
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}
	
	public boolean validateToken(String Token) {
		try {
			Jwts.parser().setSigningKey(SecurityConstant.JWT_SECRETE).parseClaimsJws(Token);
			return true;
			
		}catch (Exception e) {
			throw new AuthenticationCredentialsNotFoundException("Jwt was expired or incorrect");
		}
	}

}
