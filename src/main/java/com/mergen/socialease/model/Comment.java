package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Comment {
	
	@Id
	@GeneratedValue
	@Column
	private long commentid;
	
	private long postid;
	
	private long userid;
	
	private String content;
	
	private String timestamp;
	

	public long getCommentid() {
		return commentid;
	}

	public void setCommentid(long commentid) {
		this.commentid = commentid;
	}

	public long getPostid() {
		return postid;
	}

	public void setPostid(long postid) {
		this.postid = postid;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	
	
}
