package com.mergen.socialease.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.mergen.socialease.model.Admin;
import com.mergen.socialease.repository.AdminRepository;

public class UniqueAdminUsernameValidator implements ConstraintValidator<UniqueAdminUsername, String>{

	@Autowired
	AdminRepository adminRepository;
	
	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		try {
			Admin admin = adminRepository.findByUsername(username);
			if(admin != null) {
				return false;
			}
			return true;
		}catch(Exception e) {
			return true;
		}
		
		
	}

}

