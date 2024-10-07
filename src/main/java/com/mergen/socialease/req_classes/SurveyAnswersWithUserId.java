package com.mergen.socialease.req_classes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class SurveyAnswersWithUserId {
private long userId;
private SurveyAnswers[] surveyAnswer;
public long getUserId() {
	return userId;
}
public void setUserId(long userId) {
	this.userId = userId;
}
public SurveyAnswers[] getSurveyAnswer() {
	return surveyAnswer;
}
public void setSurveyAnswer(SurveyAnswers[] surveyAnswer) {
	this.surveyAnswer = surveyAnswer;
}


}
