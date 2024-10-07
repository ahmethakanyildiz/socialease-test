package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Review {

    @Id
    @GeneratedValue
    @Column
    Long reviewid;

    private Long clubid;

    private String comment;

    private int rating;
    
    private String userName;

    public Long getClubid(){
        return this.clubid;
    }

    public void setClubid(Long clubid){
        this.clubid = clubid;
    }

    public String getComment(){
        return this.comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public int getRating(){
        return this.rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

	public Long getReviewid() {
		return reviewid;
	}

	public void setReviewid(Long reviewid) {
		this.reviewid = reviewid;
	}
    
    
}
