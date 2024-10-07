package com.mergen.socialease.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SubClubAdminRequest {
	
	@Id
	@GeneratedValue
	@Column
	private long subClubAdminRequestid; 
	
	private long subClubid;
	
	private long userid;

	public long getSubClubAdminRequestid() {
		return subClubAdminRequestid;
	}

	public void setSubClubAdminRequestid(long subClubAdminRequestid) {
		this.subClubAdminRequestid = subClubAdminRequestid;
	}

	public long getSubClubid() {
		return subClubid;
	}

	public void setSubClubid(long subClubid) {
		this.subClubid = subClubid;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}
	
	
}
