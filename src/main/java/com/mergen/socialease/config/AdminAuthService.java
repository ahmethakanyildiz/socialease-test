package com.mergen.socialease.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mergen.socialease.model.Admin;
import com.mergen.socialease.repository.AdminRepository;

@Service
public class AdminAuthService implements UserDetailsService{

	@Autowired
	AdminRepository adminRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin inDB = adminRepository.findByUsername(username);
		System.out.println("admin auth service");
		if(inDB==null)
			throw new UsernameNotFoundException("There is no account with this username!");
		return inDB;
	}

}

