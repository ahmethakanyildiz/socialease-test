package com.mergen.socialease.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ForgotToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="token_id")
	private long tokenid;

	@Column(name="forgot_token")
	private String forgotToken;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
	
	public ForgotToken() {
	}
	
	public ForgotToken(User user) {
		this.user = user;
		createdDate = new Date();
		forgotToken = UUID.randomUUID().toString();
	}

	public String getForgotToken() {
		return forgotToken;
	}

	public void setForgotToken(String forgotToken) {
		this.forgotToken = forgotToken;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getTokenid() {
		return tokenid;
	}

	public void setTokenid(long tokenid) {
		this.tokenid = tokenid;
	}
}
