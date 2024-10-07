package com.mergen.socialease;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mergen.socialease.model.Admin;
import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.AdminRepository;
import com.mergen.socialease.repository.UserRepository;

@SpringBootApplication
public class SocialeaseApplication {

	public static void main(String[] args) {
        SpringApplication.run(SocialeaseApplication.class, args);
    }
	
	@Bean
	CommandLineRunner createInitialAdmin(AdminRepository adminRepository, UserRepository userRepository) {
		PasswordEncoder passEncoder = new BCryptPasswordEncoder();
		return (args) -> {
			Admin admin= new Admin();
			admin.setUsername("admin");
			admin.setPassword(passEncoder.encode("Root123456"));
			if(adminRepository.findByUsername("admin")==null) {
				adminRepository.save(admin);
			}
			
		};
	}
}
