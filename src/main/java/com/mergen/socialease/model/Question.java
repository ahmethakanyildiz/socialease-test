package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Question {
    
    @Id
    @GeneratedValue
    @Column
    private long questionid;

    private long clubid;

    private long subClubid;

    private String question;
    
    private float coefficient;

	public long getQuestionid() {
		return questionid;
	}

	public long getClubid() {
		return clubid;
	}

	public void setClubid(long clubid) {
		this.clubid = clubid;
	}

	public long getSubClubid() {
		return subClubid;
	}

	public void setSubClubid(long subClubid) {
		this.subClubid = subClubid;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public float getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(float coefficient) {
		this.coefficient = coefficient;
	}
	
	

    
}
