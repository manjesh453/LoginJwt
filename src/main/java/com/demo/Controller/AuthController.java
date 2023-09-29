package com.demo.Controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.Dto.AuthResponseDto;
import com.demo.Dto.LoginDto;
import com.demo.Dto.RegisterDto;
import com.demo.Repository.RoleRepository;
import com.demo.Repository.UserRepository;
import com.demo.entity.Role;
import com.demo.entity.UserEntity;
import com.demo.security.JWTGenerator;

@RestController
@RequestMapping("api/auth")
public class AuthController {
	
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private JWTGenerator jwtGenerator;
	
	@Autowired
	public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,JWTGenerator jwtGenerator) {
		this.jwtGenerator=jwtGenerator;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}
	
	@PostMapping("/register")
	public ResponseEntity<String>register(@RequestBody RegisterDto registerDto){
		if(userRepository.existsByUsername(registerDto.getUsername())) {
			return new ResponseEntity<>("Username is Taken",HttpStatus.BAD_REQUEST);
		}
		UserEntity user=new UserEntity();
		user.setUsername(registerDto.getUsername());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		
		Role roles=roleRepository.findByName("USER").get();
		user.setRoles(Collections.singletonList(roles));
		
		userRepository.save(user);
		return new ResponseEntity<>("User registered Successfully",HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto>login(@RequestBody LoginDto loginDto){
		Authentication authentication=authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token=jwtGenerator.generateToken(authentication);
		return new ResponseEntity<>(new AuthResponseDto(token),HttpStatus.OK);
		
	}
	

}
