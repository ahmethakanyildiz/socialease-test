package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.ArrayList;
import com.mergen.socialease.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Comment findByCommentid(long commentid);
	ArrayList<Comment> findAllByuserid(long userid);
}

