package com.mergen.socialease.req_classes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class SurveyAnswers {
	private long id;
	
	
	private SurveyQuestion[] questionList;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SurveyQuestion[] getQuestionList() {
		return questionList;
	}

	public void setQuestionList(SurveyQuestion[] questionList) {
		this.questionList = questionList;
	}
	
}
