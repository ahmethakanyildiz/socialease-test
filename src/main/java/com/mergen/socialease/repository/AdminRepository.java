package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mergen.socialease.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{
	Admin findByUsername(String username);
}
