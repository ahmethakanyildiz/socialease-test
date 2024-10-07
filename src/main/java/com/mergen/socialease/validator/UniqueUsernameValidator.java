package com.mergen.socialease.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.UserRepository;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String>{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		try {
			User user = userRepository.findByUsername(username);
			if(user != null) {
				return false;
			}
			return true;
		}catch(Exception e) {
			return true;
		}
		
		
	}

}

