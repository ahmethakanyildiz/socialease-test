package com.mergen.socialease.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mergen.socialease.model.Admin;
import com.mergen.socialease.model.SubClub;
import com.mergen.socialease.model.SubClubAdminRequest;
import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.SubClubAdminRequestRepository;
import com.mergen.socialease.repository.SubClubRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;

@RestController
public class SubClubAdminRequestController {
	
	@Autowired
	private SubClubAdminRequestRepository subClubAdminRequestRepository;
	
	@Autowired
	private SubClubRepository subClubRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/saveadminrequest")
	public GenericResponse saveSubClubAdminRequest(@RequestBody SubClubAdminRequest adminRequest,@CurrentUser User u) {
		
		
		long subClubid = adminRequest.getSubClubid();
		long userid = adminRequest.getUserid();
		if(u.getUserid()!=userid || userRepository.findById(userid)==null) {
			return new GenericResponse("Error: You are a hacker!");
		}
		SubClub subClub = subClubRepository.findBysubClubid(subClubid);
		User user = userRepository.findByUserid(userid);
		
		boolean isSubClubMember = false;
		
		for(String uid: subClub.getUserList().split(",")) {
			if(Long.parseLong(uid) == userid) {
				isSubClubMember = true;
				break;
			}
		}
		
		if(!isSubClubMember) {
			return new GenericResponse("Error: You must be member of this subclub");
		}

		if(subClub.getAdminid()!=null && subClub.getAdminid() == userid) {
			return new GenericResponse("Error: You are already admin of this subclub");
		}
		
		if(user.getIsSubClubAdmin() != -1 ) {
			if(user.getIsSubClubAdmin() == -2){
				return new GenericResponse("Error: You cannot send request while you are waiting for a subclub admin request!");
			}
			else if(user.getIsSubClubAdmin() == -3) {
				return new GenericResponse("Error: You cannot be a subclub admin, sorry :(((");
			}
			else {
				return new GenericResponse("Error: You cannot send request while you are admin of another subclub.");
			}
			
		}
		
		subClubAdminRequestRepository.save(adminRequest);
		user.setIsSubClubAdmin(-2);
		userRepository.save(user);
		
		return new GenericResponse("Request to be subclub admin is successfully sent!");
	}
	
	
	@SuppressWarnings("unchecked")
	@GetMapping("/mergen/admin/getadminrequests")
	public JSONArray getAdminRequests() {
		
		JSONArray requests = new JSONArray();
		
		for(SubClubAdminRequest request : subClubAdminRequestRepository.findAll()) {
			JSONObject requestJson = new JSONObject();
			requestJson.put("subClubAdminRequestid", request.getSubClubAdminRequestid());
			requestJson.put("subClubid", request.getSubClubid());
			requestJson.put("userid", request.getUserid());
			User user = userRepository.findByUserid(request.getUserid());
			requestJson.put("username", user.getUsername());
			SubClub subClub = subClubRepository.findBysubClubid(request.getSubClubid());
			requestJson.put("subClubName", subClub.getName());
			String currentAdmin="";
			if(subClub.getAdminid()!=null) {
				currentAdmin=userRepository.findByUserid(subClub.getAdminid()).getUsername();
			}
			requestJson.put("currentAdmin",currentAdmin);
			requests.add(requestJson);
		}
		
		return requests;
	}
	
	@PostMapping("/mergen/admin/answeradminrequest")
	public GenericResponse deleteAdminRequest(@RequestBody JSONObject params,@CurrentUser Admin a) {

		Long longid = Long.valueOf((Integer) params.get("id"));
		Long mode = Long.valueOf((Integer) params.get("mode"));
		if(mode==0) {
			SubClubAdminRequest req = subClubAdminRequestRepository.findBysubClubAdminRequestid(longid);
			Long requesterId=req.getUserid();
			User requester=userRepository.findByUserid(requesterId);
			requester.setIsSubClubAdmin(-1);
			userRepository.save(requester);
			try {
				subClubAdminRequestRepository.deleteById(longid);
			} catch (Exception e) {
				return new GenericResponse("Sub Club Admin Request could not be deleted");
			}

			return new GenericResponse("Sub Club Admin Request deleted");
		}
		else {
			SubClubAdminRequest req = subClubAdminRequestRepository.findBysubClubAdminRequestid(longid);
			Long scId=req.getSubClubid();
			SubClub sc = subClubRepository.findBysubClubid(scId);
			Long requesterId=req.getUserid();
			User requester=userRepository.findByUserid(requesterId);
			if(sc.getAdminid()==null) {
				requester.setIsSubClubAdmin(scId);
				userRepository.save(requester);
				sc.setAdminid(requesterId);
				subClubRepository.save(sc);	
			}
			else {
				User oldAdmin = userRepository.findByUserid(sc.getAdminid());
				oldAdmin.setIsSubClubAdmin(-1);
				userRepository.save(oldAdmin);
				requester.setIsSubClubAdmin(scId);
				userRepository.save(requester);
				sc.setAdminid(requesterId);
				subClubRepository.save(sc);
			}
			try {
				subClubAdminRequestRepository.deleteById(longid);
			} catch (Exception e) {
				return new GenericResponse("Sub Club Admin Request answered but could not be deleted");
			}
			return new GenericResponse("Sub Club Admin Request answered");
		}
		
	}
}
