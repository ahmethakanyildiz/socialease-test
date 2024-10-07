package com.mergen.socialease.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.UserRepository;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String>{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		try {
			User user = userRepository.findByEmail(email);
			if(user != null) {
				return false;
			}
			return true;
		}catch(Exception e) {
			return true;
		}
	}

}

