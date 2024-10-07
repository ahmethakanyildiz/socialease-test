package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mergen.socialease.model.Club;

public interface ClubRepository extends JpaRepository<Club, Long> {
	Club findByclubid(long clubid);
	Club findByClubName(String name);
}

