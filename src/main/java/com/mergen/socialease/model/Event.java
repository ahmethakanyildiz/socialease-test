package com.mergen.socialease.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Event {
	
	@Id
	@GeneratedValue
	@Column
	private long eventid;
	
	private long subclubid;
	
	private String title;
	
	private String content;

	public long getSubclubid() {
		return subclubid;
	}

	public void setSubclubid(long subclubid) {
		this.subclubid = subclubid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getEventid() {
		return eventid;
	}

	public void setEventid(long eventid) {
		this.eventid = eventid;
	}
	
	
	
}
