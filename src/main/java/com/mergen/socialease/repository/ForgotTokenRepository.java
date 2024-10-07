package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mergen.socialease.model.ForgotToken;

public interface ForgotTokenRepository extends JpaRepository<ForgotToken, Long> {
	ForgotToken findByforgotToken(String forgotToken);
}
