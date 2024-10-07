package com.mergen.socialease.controller;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.mergen.socialease.model.Admin;
import com.mergen.socialease.model.Club;
import com.mergen.socialease.model.SubClub;
import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.AdminRepository;
import com.mergen.socialease.repository.ClubRepository;
import com.mergen.socialease.repository.SubClubRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;
import com.mergen.socialease.shared.Views;

@RestController
public class AdminController {
	
	@SuppressWarnings("unused")
	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private SubClubRepository subClubRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ClubRepository clubRepository;
	
	PasswordEncoder passEncoder = new BCryptPasswordEncoder();
	
	@PostMapping("/mergen/admin/login")
	@JsonView(Views.Base.class)
	public ResponseEntity<?> adminLogin(@CurrentUser Admin admin){
		return ResponseEntity.ok(admin);
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/mergen/admin/getsubclubadmins")
	public JSONArray getSubClubAdmins(@CurrentUser Admin admin){
		ArrayList<User> list = userRepository.findAllByisSubClubAdminGreaterThan((long)0);
		JSONArray list2 = new JSONArray();
		for(User u : list)
			list2.add(getSingleUser(u.getUserid()));
		return list2;
	}

	@PostMapping("/mergen/admin/bansubclubadmins")
	public GenericResponse banSubClubAdmins(@RequestBody JSONObject json, @CurrentUser Admin admin){

		if(!((Long.valueOf((Integer) json.get("banType")))==-1 || (Long.valueOf((Integer) json.get("banType")))==-3)) {
			return new GenericResponse("Error: You are a hacker!");
		}
		User u = userRepository.findByUserid(Long.valueOf((Integer) json.get("userid")));
		SubClub sc = subClubRepository.findBysubClubid(u.getIsSubClubAdmin());
		sc.setAdmin(null);
		sc.setAdminid(null);
		u.setIsSubClubAdmin(Long.valueOf((Integer) json.get("banType")));
		userRepository.save(u);
		subClubRepository.save(sc);
		return new GenericResponse("Successful: SubClub Admin Banned");
	}
	
	@GetMapping("/mergen/admin/getuserwithmode")
	public JSONArray getUserForAdmin(@RequestParam Long mode, Long id, @CurrentUser Admin admin){
		if(id<0)
			throw new Error();
		if(mode == 1)
			return getClubUsers(id);
		else if(mode == 2)
			return getSubClubUsers(id);
		else if(mode == 3)
			return getAllUsers();
		else
			throw new Error();
	}

	@SuppressWarnings("unchecked")
	private JSONObject getSingleUser(Long userId){
		User user = userRepository.findByUserid(userId);
		JSONObject userJson = new JSONObject();

		userJson.put("id",user.getUserid());
		userJson.put("username",user.getUsername());
		
		SubClub sc = subClubRepository.findBysubClubid(user.getIsSubClubAdmin());
		userJson.put("subclubName",sc.getName());

		return userJson;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getSingleUser2(Long userId){
		User user = userRepository.findByUserid(userId);
		JSONObject userJson = new JSONObject();

		userJson.put("id",user.getUserid());
		userJson.put("username",user.getUsername());

		return userJson;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getAllUsers(){
		JSONArray allUsers = new JSONArray();

		try {
			for(User u : userRepository.findAll()){
				allUsers.add(getSingleUser2(u.getUserid()));
			}
		}catch(Exception e) {
		}

		return allUsers;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getClubUsers(Long clubId){
		JSONArray clubUsers = new JSONArray();
		Club c = clubRepository.findByclubid(clubId);

		try {
			for(String s : c.getUserList().split(","))
				clubUsers.add(getSingleUser2(Long.parseLong(s)));
		}catch(Exception e) {

		}
		
		return clubUsers;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getSubClubUsers(Long subClubId){

		JSONArray subClubUsers = new JSONArray();
		SubClub sc = subClubRepository.findBysubClubid(subClubId);

		try {
			for(String s : sc.getUserList().split(","))
				subClubUsers.add(getSingleUser2(Long.parseLong(s)));
		}catch(Exception e) {

		}
		
		return subClubUsers;

	}

}
