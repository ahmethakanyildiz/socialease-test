package com.mergen.socialease.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonView;
import com.mergen.socialease.shared.Views;
import com.mergen.socialease.validator.UniqueAdminUsername;

@Entity
public class Admin implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4391499725634008684L;

	@Id
	@GeneratedValue
	@Column(name="admin_id")
	private long adminid;
	
	@NotNull
	@Size(min=5, max=255)
	@JsonView(Views.Base.class)
	@UniqueAdminUsername
	@Pattern(regexp = "[a-zA-Z\\d\\u002e]*")
	private String username;
	
	@NotNull
	@Size(min=5, max=255)
	@JsonView(Views.Base.class)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")
	@Pattern(regexp ="[^:\\s]*")
	private String password;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("Role_admin");
	}
	
	public long getAdminid() {
		return adminid;
	}

	public void setAdminid(long adminid) {
		this.adminid = adminid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
}
