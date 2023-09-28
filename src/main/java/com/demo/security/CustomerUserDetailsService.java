package com.demo.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.demo.Repository.UserRepository;
import com.demo.entity.UserEntity;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    
	
	private UserRepository userRepository;
	
	@Autowired
	public CustomerUserDetailsService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("UserName not found"));
		
		return new User(user.getUsername(),user.getPassword(),mapRolesTOAuthorities(user.getRoles()));
	}
	
	private Collection<GrantedAuthority>mapRolesTOAuthorities(List<com.demo.entity.Role> list){
		return list.stream().map(role->new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

}
