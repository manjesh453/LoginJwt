package com.demo.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter{

	
	private JWTGenerator jwtGenerator;
	
	private CustomerUserDetailsService customerUserDetailsService;
	


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token=getJWTfromToken(request);
		if(StringUtils.hasText(token)&&jwtGenerator.validateToken(token)) {
			String username=jwtGenerator.getusernameFromJWT(token);
			UserDetails userDetails=customerUserDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			
		}
		filterChain.doFilter(request, response);
		
	}
	
	private String getJWTfromToken(HttpServletRequest request) {
		String bearertoken=request.getHeader("Authorization");
		if(StringUtils.hasText(bearertoken)&&bearertoken.startsWith("Bearer ")) {
			return bearertoken.substring(7,bearertoken.length());
		}
		return null;
	}

}
