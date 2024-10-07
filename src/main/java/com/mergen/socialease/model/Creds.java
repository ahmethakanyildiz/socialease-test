package com.mergen.socialease.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonView;
import com.mergen.socialease.shared.Views;

public class Creds {
    
    @NotNull
    @Size(min=5, max=255)
    @JsonView(Views.Base.class)
    private String username;

    @NotNull
	@Size(min=5, max=255)
	@JsonView(Views.Base.class)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="The password must contain at least one uppercase letter, one lowercase letter and one number.")
	@Pattern(regexp ="[^:\\s]*", message="Password must not contain space and ':' characters.")
    private String password;

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
