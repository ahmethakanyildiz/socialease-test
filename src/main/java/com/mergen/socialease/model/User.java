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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.mergen.socialease.shared.Views;
import com.mergen.socialease.validator.UniqueUsername;
import com.mergen.socialease.validator.UniqueEmail;
import javax.persistence.Lob;

@Entity
public class User implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3180518162729274487L;

	@Id
	@GeneratedValue
	@Column(name="user_id")
	@JsonView(Views.Base.class)
	private long userid;
	
	@NotNull
	@Size(min=5, max=255)
	@UniqueEmail
	@JsonView(Views.Base.class)
	@Pattern(regexp="^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", message="Invalid email address")
	private String email;
	
	@NotNull
	@Size(min=5, max=255)
	@JsonView(Views.Base.class)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="The password must contain at least one uppercase letter, one lowercase letter and one number.")
	@Pattern(regexp ="[^:\\s]*", message="Password must not contain space and ':' characters.")
	private String password;
		
	@NotNull
	@Size(min=5, max=255)
	@JsonView(Views.Base.class)
	private String displayName;

	@NotNull
	@Size(min=5, max=255)
	@UniqueUsername
	@JsonView(Views.Base.class)
	@Pattern(regexp = "[a-zA-Z\\d\\u002e]*", message="Username can only contain uppercase letters, lowercase letters, numbers or '.' character.")
	private String username;
	
	@JsonProperty("isRegistered")
	@JsonView(Views.Base.class)
	private boolean isRegistered;
	
	@JsonProperty("isSurveyAnswered")
	@JsonView(Views.Base.class)
	private boolean isSurveyAnswered;
	
	@JsonProperty("isThereNewClub")
	@JsonView(Views.Base.class)
	private boolean isThereNewClub;
	
	private String clubList;
	
	private String subClubList;
	
	private String newClubList;
	
	private String biographi;
	
	private String postList;
	
	private String commentList;
	
	private String likeList;
	
	private long totalPostCount;
	
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private String image;

	public String getBiographi() {
		return biographi;
	}

	public void setBiographi(String biographi) {
		this.biographi = biographi;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNewClubList() {
		return newClubList;
	}

	public void setNewClubList(String newClubList) {
		this.newClubList = newClubList;
	}

	public String getNewSubClubList() {
		return newSubClubList;
	}

	public void setNewSubClubList(String newSubClubList) {
		this.newSubClubList = newSubClubList;
	}

	public boolean isThereNewClub() {
		return isThereNewClub;
	}

	public void setIsThereNewClub(boolean isThereNewClub) {
		this.isThereNewClub = isThereNewClub;
	}

	private String newSubClubList;
	

	
	@JsonView(Views.Base.class)
	private long isSubClubAdmin;

	//comes from UserDetails
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("Role_user");
	}

	public long getUserid() {
		return userid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	public boolean isSurveyAnswered() {
		return isSurveyAnswered;
	}

	public void setSurveyAnswered(boolean isSurveyAnswered) {
		this.isSurveyAnswered = isSurveyAnswered;
	}

	public String getClubList() {
		return clubList;
	}

	public void setClubList(String clubList) {
		this.clubList = clubList;
	}

	public String getSubClubList() {
		return subClubList;
	}

	public void setSubClubList(String subClubList) {
		this.subClubList = subClubList;
	}
	
	public long getIsSubClubAdmin() {
		return isSubClubAdmin;
	}

	public void setIsSubClubAdmin(long isSubClubAdmin) {
		this.isSubClubAdmin = isSubClubAdmin;
	}
	
	//functions comes from UserDetails

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isRegistered;
	}

	public String getPostList() {
		return postList;
	}

	public void setPostList(String postList) {
		this.postList = postList;
	}

	public String getCommentList() {
		return commentList;
	}

	public void setCommentList(String commentList) {
		this.commentList = commentList;
	}

	public String getLikeList() {
		return likeList;
	}

	public void setLikeList(String likeList) {
		this.likeList = likeList;
	}

	public long getTotalPostCount() {
		return totalPostCount;
	}

	public void setTotalPostCount(long totalPostCount) {
		this.totalPostCount = totalPostCount;
	}

}
