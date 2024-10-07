package com.mergen.socialease.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Club{

    @Id
    @GeneratedValue
    @Column
    private long clubid;

    private String clubName;

    private String subClubList;

    private String questionList;
    
    private String userList;

	public long getClubid() {
		return clubid;
	}

	public String getClubName() {
		return clubName;
	}


	public void setClubName(String clubName) {
		this.clubName = clubName;
	}


	public String getSubClubList() {
		return subClubList;
	}


	public void setSubClubList(String subClubList) {
		this.subClubList = subClubList;
	}


	public String getQuestionList() {
		return questionList;
	}


	public void setQuestionList(String questionList) {
		this.questionList = questionList;
	}

	public String getUserList() {
		return userList;
	}

	public void setUserList(String userList) {
		this.userList = userList;
	}

	
}