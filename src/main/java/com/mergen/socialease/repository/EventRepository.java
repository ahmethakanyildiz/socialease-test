package com.mergen.socialease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mergen.socialease.model.Event;


public interface EventRepository extends JpaRepository<Event, Long>{
	Event findByEventid(long eventid);
	List<Event> findBySubclubid(long subclubid);
}
