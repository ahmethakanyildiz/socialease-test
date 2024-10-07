package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ClubRequest {
	
	@Id
	@GeneratedValue
	@Column
	private long clubRequestid;
	
	private long userid;
	
	private String name;
	
	private String  content;

	public long getClubRequestid() {
		return clubRequestid;
	}

	public void setClubRequestid(long clubRequestid) {
		this.clubRequestid = clubRequestid;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
