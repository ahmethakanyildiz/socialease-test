package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SubClub {

    @Id
    @GeneratedValue
    @Column
    private long subClubid;

    private String name;

    private String admin;
    
    private Long adminid;

    private long clubid;
    
    private String userList;
    
    private String postList;
    
    private String eventList;

	public long getSubClubid() {
		return subClubid;
	}

	public String getName() {
		return name;
	}

	public Long getAdminid() {
		return adminid;
	}

	public void setAdminid(Long adminid) {
		this.adminid = adminid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public long getClubid() {
		return clubid;
	}

	public void setClubid(long clubid) {
		this.clubid = clubid;
	}

	public String getUserList() {
		return userList;
	}

	public void setUserList(String userList) {
		this.userList = userList;
	}

	public String getPostList() {
		return postList;
	}

	public void setPostList(String postList) {
		this.postList = postList;
	}

	public String getEventList() {
		return eventList;
	}

	public void setEventList(String eventList) {
		this.eventList = eventList;
	}
    
}
