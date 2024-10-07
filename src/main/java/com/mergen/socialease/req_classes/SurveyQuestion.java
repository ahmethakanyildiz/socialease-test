package com.mergen.socialease.req_classes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class SurveyQuestion {
	private long id;
	private int answer;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getAnswer() {
		return answer;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}
}
