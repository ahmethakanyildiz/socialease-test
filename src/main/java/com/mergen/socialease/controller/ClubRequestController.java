package com.mergen.socialease.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mergen.socialease.model.Admin;
import com.mergen.socialease.model.ClubRequest;
import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.ClubRequestRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;

@RestController
public class ClubRequestController {

	@Autowired
	private ClubRequestRepository clubRequestRepository;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/saveclubrequest")
	public GenericResponse saveClubRequest(@RequestBody ClubRequest clubRequest, @CurrentUser User user) {
		if (user.getUserid() != clubRequest.getUserid()
				|| userRepository.findByUserid(clubRequest.getUserid()) == null) {
			return new GenericResponse("Error: You are a hacker!");
		}
		if (clubRequest.getName() == null || clubRequest.getName().equals("") || clubRequest.getContent() == null
				|| clubRequest.getContent().equals("")) {
			if ((clubRequest.getName() == null || clubRequest.getName().equals(""))
					&& (clubRequest.getContent() == null || clubRequest.getContent().equals(""))) {
				return new GenericResponse("invalid-name invalid-content");
			} else if (clubRequest.getName() == null || clubRequest.getName().equals("")) {
				return new GenericResponse("invalid-name");
			} else if (clubRequest.getContent() == null || clubRequest.getContent().equals("")) {
				return new GenericResponse("invalid-content");
			}
		}
		clubRequestRepository.save(clubRequest);
		return new GenericResponse("Club Request sent successfully!");
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/mergen/admin/getclubrequests")
	public JSONArray getClubRequests(@CurrentUser Admin a) {

		JSONArray clubRequests = new JSONArray();

		for (ClubRequest clubRequest : clubRequestRepository.findAll()) {
			JSONObject clubRequestJson = new JSONObject();

			clubRequestJson.put("clubRequestid", clubRequest.getClubRequestid());
			clubRequestJson.put("content", clubRequest.getContent());
			clubRequestJson.put("name", clubRequest.getName());
			clubRequestJson.put("userid", clubRequest.getUserid());

			User user = userRepository.findByUserid(clubRequest.getUserid());
			clubRequestJson.put("username", user.getUsername());

			clubRequests.add(clubRequestJson);
		}

		return clubRequests;

	}

	@PostMapping("/mergen/admin/deleteclubrequest")
	public GenericResponse deleteClubRequest(@RequestBody JSONObject id,@CurrentUser Admin a) {

		Long longid = Long.valueOf((Integer) id.get("id"));

		try {
			clubRequestRepository.deleteById(longid);
		} catch (Exception e) {
			return new GenericResponse("Club Request could not be deleted");
		}

		return new GenericResponse("Club request deleted");
	}
}
