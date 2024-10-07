package com.mergen.socialease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mergen.socialease.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
	Post findByPostid(long postid);
	List<Post> findBySubclubidOrderByTimestamp(long subClubid);
	List<Post> findByUseridOrderByTimestamp(long userid);
	List<Post> findBySubclubid(long subclubid);
	List<Post> findByUserid(long userid);
}

