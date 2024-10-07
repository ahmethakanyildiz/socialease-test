package com.mergen.socialease.controller;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mergen.socialease.model.Comment;
import com.mergen.socialease.model.Post;
import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.CommentRepository;
import com.mergen.socialease.repository.PostRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;

@RestController
public class CommentController {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@PutMapping("/makecomment")
	public GenericResponse makeComment(@RequestBody Comment comment, @CurrentUser User currentUser) {
		
		String content = comment.getContent();
		long userid = comment.getUserid();
		long postid = comment.getPostid();
		
		
		if(currentUser.getUserid()!= userid || userRepository.findByUserid(userid)==null) {
			
			return new GenericResponse("Error: You are not allowed to share this comment");
			
		}
		
		if(content== null|| content.equals("")) {
			return new GenericResponse("Error: You cannot make empty comments");
		}
		
		User user = userRepository.findByUserid(userid);
		Post post = postRepository.findByPostid(postid);
		
		if(user==null || post==null) {
			return new GenericResponse("Error: You cannot share comment ");
		}
	
		
		commentRepository.save(comment);
		
		String userCommentList = user.getCommentList();
		String postCommentList = post.getCommentList();
		
		if(userCommentList == null) {
			 userCommentList= Long.toString(comment.getCommentid());
		}
		else {
			userCommentList = userCommentList + "," + comment.getCommentid();
		}
		
		if(postCommentList == null) {
			postCommentList = Long.toString(comment.getCommentid());
		}
		else {
			postCommentList= postCommentList + "," + comment.getCommentid();
		}
		
		user.setCommentList(userCommentList);
		userRepository.save(user);
		post.setCommentList(postCommentList);
		postRepository.save(post);
		
		
		return new GenericResponse("Comment is published ");
		
	}
	
	
	@PutMapping("/deletecomment")
	private GenericResponse deleteComment(@RequestBody JSONObject json, @CurrentUser User currentUser) {
		
		long commentid = Long.valueOf((Integer) json.get("cid"));
		Comment deletedComment = commentRepository.findByCommentid(commentid);
		
		if(deletedComment == null) {
			return new GenericResponse("Error: Comment could not be found");
		}
		
		if(currentUser.getUserid() != deletedComment.getUserid()) {
			return new GenericResponse("Error: You are not allowed to delete this comment");
		}
		
		
		//Delete comment from user's comment list
		
		User commenterUser = userRepository.findByUserid(deletedComment.getUserid());
		String userCommentList = commenterUser.getCommentList();
		
		if(userCommentList==null) {
			return new GenericResponse("Error: Something went wrong while deleting the comment");
		}
		
		commenterUser.setCommentList(removeFromStringList(userCommentList, Long.toString(commentid)));
		
		// Delete comment from Post's commentList
		Post post = postRepository.findByPostid(deletedComment.getPostid());
		
		String postCommentList = post.getCommentList();
		
		if(postCommentList == null) {
			return new GenericResponse("Error: Something went wrong while deleting the comment");
		}
		
		post.setCommentList(removeFromStringList(postCommentList, Long.toString(commentid)));
		
		userRepository.save(commenterUser);
		postRepository.save(post);
		
		commentRepository.delete(deletedComment);
		
		return new GenericResponse("Comment is successfully deleted");
		
	}
	
	
	private String removeFromStringList(String strList, String removedid) {
		
		
		String[] ids = strList.split(",");
		
		ArrayList<String> newids = new ArrayList<String>();
		
		for(int i = 0; i< ids.length ; i++) {
			
			if(Long.parseLong(ids[i]) != Long.parseLong(removedid)) {
				newids.add(ids[i]);
			}
			
		}
	
		
		if(newids.isEmpty()) {
			return null;
		}
		
		else {
			
	        StringBuilder str = new StringBuilder("");
	        
	        for (String newid : newids) {
	  
	            str.append(newid).append(",");
	        }
	  
	        String commaseparatedlist = str.toString();
	  
	        if (commaseparatedlist.length() > 0)
	            commaseparatedlist
	                = commaseparatedlist.substring(
	                    0, commaseparatedlist.length() - 1);
	        
	        return commaseparatedlist;
		}
	}
	
}

