package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.ArrayList;

import com.mergen.socialease.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
	User findByUsername(String username);
	User findByUserid(Long userid);
	ArrayList<User> findAllByisSubClubAdminGreaterThan(Long isSubClubAdmin);

}
