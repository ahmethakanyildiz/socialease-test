package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mergen.socialease.model.SubClub;

public interface SubClubRepository extends JpaRepository<SubClub, Long> {
	SubClub findBysubClubid(long subClubid);
	SubClub findByName(String name);
}

