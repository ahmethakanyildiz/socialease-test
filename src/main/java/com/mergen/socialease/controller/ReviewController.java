package com.mergen.socialease.controller;

import java.util.ArrayList;

import com.mergen.socialease.model.Review;
import com.mergen.socialease.shared.GenericResponse;

import com.mergen.socialease.repository.ClubRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.repository.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

	@SuppressWarnings("unused")
	@Autowired
	private ClubRepository clubRepository;

	@SuppressWarnings("unused")
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@PostMapping("/review")
	public GenericResponse review(@RequestBody Review review) {
		try {
			boolean isThereReview=false;
			java.util.List<Review> reviewList = reviewRepository.findAll();
			for(int j=0;j<reviewList.size();j++) {
				if(reviewList.get(j).getUserName().equals(review.getUserName()) && reviewList.get(j).getClubid()==review.getClubid()) {
					isThereReview=true;
				}
			}
			if (isThereReview) {
				return new GenericResponse("Error: You have already sent a review!");
			} else {
				reviewRepository.save(review);
				return new GenericResponse("Review Shared");
			}
		} catch (Exception e) {
			return new GenericResponse("Error: Something is wrong!");
		}
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("/viewreviews")
	public ArrayList getReviews(@RequestParam Long clubid) {
		return reviewRepository.findAllByClubid(clubid);
	}

	@GetMapping("/checkmyreview")
	public GenericResponse checkMyReview(@RequestParam String u, String i) {
		try {
			java.util.List<Review> reviewList = reviewRepository.findAll();
			for(int j=0;j<reviewList.size();j++) {
				if(reviewList.get(j).getUserName().equals(u) && reviewList.get(j).getClubid()==Integer.parseInt(i)) {
					return new GenericResponse("VAR");
				}
			}
		} catch (Exception e) {
			return new GenericResponse("YOK");
		}
		return new GenericResponse("YOK");
	}
}