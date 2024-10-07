package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mergen.socialease.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	Question findByquestionid(long questionid);
	Question findByQuestion(String q);
}

