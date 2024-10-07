package com.mergen.socialease.error;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.mergen.socialease.shared.Views;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	@JsonView(Views.Base.class)
	private int status;
	@JsonView(Views.Base.class)
	private String message;
	@JsonView(Views.Base.class)
	private String path;
	@JsonView(Views.Base.class)
	private long timestamp = new Date().getTime();
	private Map<String, String> validationErrors;
	
	public ErrorResponse(int status, String message, String path) {
		this.status=status;
		this.message=message;
		this.path=path;
		
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public Map<String, String> getValidationErrors() {
		return validationErrors;
	}
	public void setValidationErrors(Map<String, String> validationErrors) {
		this.validationErrors = validationErrors;
	}
	
}