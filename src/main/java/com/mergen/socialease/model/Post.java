package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Post {
	
	@Id
	@GeneratedValue
	@Column
	private long postid;
	
	private long userid;
	
	private long subclubid;
	
	private long clubid;
	
	private String commentList;
	
	private String likeList;
	
	private int likeCount;
	
	private String content;
	
	@Lob
    @Column(columnDefinition = "LONGBLOB")
    private String image;
	
	private String timestamp;
	
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
	public long getSubclubid() {
		return subclubid;
	}
	public void setSubclubid(long subclubid) {
		this.subclubid = subclubid;
	}
	public long getClubid() {
		return clubid;
	}
	public void setClubid(long clubid) {
		this.clubid = clubid;
	}
	public String getCommentList() {
		return commentList;
	}
	public void setCommentList(String commentList) {
		this.commentList = commentList;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLikeList() {
		return likeList;
	}
	public void setLikeList(String likeList) {
		this.likeList = likeList;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	
	
	
}
