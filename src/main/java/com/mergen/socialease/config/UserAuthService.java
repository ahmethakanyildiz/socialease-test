package com.mergen.socialease.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.UserRepository;

@Service
public class UserAuthService implements UserDetailsService{

	@Autowired
	UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User inDB=userRepository.findByUsername(username);
		System.out.println("user auth service");
		if(inDB==null)
			throw new UsernameNotFoundException("There is no account with this username!");
		return inDB;
	}

}
