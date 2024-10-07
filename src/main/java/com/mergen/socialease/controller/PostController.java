package com.mergen.socialease.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mergen.socialease.model.Comment;
import com.mergen.socialease.model.Post;
import com.mergen.socialease.model.SubClub;
import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.CommentRepository;
import com.mergen.socialease.repository.PostRepository;
import com.mergen.socialease.repository.SubClubRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;

@RestController
public class PostController {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SubClubRepository subClubRepository;
	
	
	@PostMapping("/createpost")
	public GenericResponse createPost (@RequestBody Post post, @CurrentUser User currentUser ) {
		
		
		String errMsg1 = "You are not member of this sub club";
		String errMsg2 = "You cannot share post without any content";
		try {
			
			long userid =post.getUserid();
			long subClubid = post.getSubclubid();
			
			if(currentUser.getUserid() != userid || userRepository.findByUserid(currentUser.getUserid()) == null) {
				return new GenericResponse("You are not permitted to share a post");
			}
			
			User user =userRepository.findByUserid(userid);
			SubClub subClub = subClubRepository.findBysubClubid(subClubid); 
			
			if(user == null || subClub == null) {
				
				return new GenericResponse("You cannot share post");
			}
			
			String subClubUserList =subClub.getUserList();
			
			if(subClubUserList.equals("") || subClubUserList == null) {
				return new GenericResponse(errMsg1);
			}
			
			boolean isSubClubMember = false;
				
			for (String uid : subClubUserList.split(",")) {
					
				if(userid == Long.parseLong(uid)) {
						isSubClubMember = true;
				}
					
			}
				
			if(isSubClubMember == false) {
				return new GenericResponse(errMsg1);
			}
			
			if(post.getContent() == null || post.getContent().equals("")) {
				return new GenericResponse(errMsg2);
			}
			
			
			String base64Image = (String) post.getImage();
			
			if(base64Image != null) {
				String fileType = base64Image.split(";")[0];
					
				if (!("data:image/png".equals(fileType) || "data:image/jpeg".equals(fileType) || "data:image/jpg".equals(fileType)) ) {
				
					return new GenericResponse("Error: Image format can be jpg, jpeg or png!");
				
				}
			
			}
			
			postRepository.save(post);
			
			String subClubPostList = subClub.getPostList();
			String userPostList = user.getPostList();
			
			// Add post to sub club
			if(subClubPostList == null) {
				 subClubPostList= Long.toString(post.getPostid());
			}
			else {
				subClubPostList = subClubPostList+ "," + post.getPostid();
			}
			
			//Add post to user
			if(userPostList == null) {
				userPostList = Long.toString(post.getPostid());
			}
			else {
				userPostList = userPostList + "," + post.getPostid();
			}
			
			subClub.setPostList(subClubPostList);
			subClubRepository.save(subClub);
			user.setPostList(userPostList);
			user.setTotalPostCount(user.getTotalPostCount()+1);
			userRepository.save(user);
			
			return new GenericResponse("Post successfully shared");
			
		}
		
		catch(Exception e)  {
			
			return new GenericResponse("Post could not be published");
		}
		
		
	}
	
	
	
	@PutMapping("/deletepost")
	public GenericResponse deletePost(@RequestBody JSONObject id,@CurrentUser User currentUser) {
		
		try {
		long postid =Long.valueOf((Integer)id.get("postid"));
		Post deletedPost = postRepository.findByPostid(postid);
		
		if(deletedPost == null ) {
			return new GenericResponse("Error: Post could not be found");
		}
	
		if(currentUser.getUserid() != deletedPost.getUserid()) {
			return new GenericResponse("Error: You are not allowed to delete this post");
		}
		
		// Delete Comments of the post
		
		String commentList = deletedPost.getCommentList();
		
		if( commentList != null) {

			for(String commentid:commentList.split(",")) {
				
				Comment deletedComment = commentRepository.findByCommentid(Long.valueOf(commentid));
				User commenterUser =userRepository.findByUserid(deletedComment.getUserid());
				String userCommentList = commenterUser.getCommentList();
		        
			    commenterUser.setCommentList(removeFromStringList(userCommentList, commentid));
			    userRepository.save(commenterUser);
				commentRepository.deleteById(Long.parseLong(commentid));
				
			}
			
		}
		
		//Userların like Listini güncelle
		
		String likeList = deletedPost.getLikeList();
		
		if(likeList != null) {
			
			for(String likeruserid: likeList.split(",")) {
				User likerUser = userRepository.findByUserid(Long.parseLong(likeruserid));
				String userLikeList = likerUser.getLikeList();
				
				likerUser.setLikeList(removeFromStringList(userLikeList,Long.toString(postid)));
				userRepository.save(likerUser);
			}
			
		}
		
		//Userdan postu sil
		
		User user = userRepository.findByUserid(deletedPost.getUserid());   
		
		String userPostList = user.getPostList();
		
		user.setPostList(removeFromStringList(userPostList, Long.toString(postid)));
		
		// Update subclub's post list
		
		SubClub subClub = subClubRepository.findBysubClubid(deletedPost.getSubclubid());
		
		String subClubPostList =subClub.getPostList();
		
		subClub.setPostList(removeFromStringList(subClubPostList, Long.toString(postid)));
		
		user.setTotalPostCount(user.getTotalPostCount()-1);
		
		subClubRepository.save(subClub);
		userRepository.save(user);
		postRepository.delete(deletedPost);
		
		return new GenericResponse("Post successfully deleted");
		
	}
		
		catch(Exception e) {
			e.printStackTrace();
			return new GenericResponse("Error: Something went wrong while deleting the post");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/{subclubid}/getposts")
	public JSONArray getPostsinSubClub(@PathVariable long subclubid, @CurrentUser User currentUser) {
		

		List<Post> postPage =  postRepository.findBySubclubidOrderByTimestamp(subclubid);
		
		long currentUserid = currentUser.getUserid();
		
		
		JSONArray posts = new JSONArray();
		
		SubClub subClub =subClubRepository.findBysubClubid(subclubid);
		String subClubUserList = subClub.getUserList();
		
		boolean isSubClubMember = false;
	
		
		if(subClubUserList != null) {
			for(String subClubUser :subClubUserList.split(",")) {
				
				if(Long.parseLong(subClubUser) == currentUser.getUserid()) {
					isSubClubMember = true;
				}
			}
		}
	
		
		if(isSubClubMember == false) {
			return null;
		}
		
		else {
			
			
		for(Post post: postPage) {
			
			JSONObject postJson = new JSONObject();
			
			String likeList = post.getLikeList();
			boolean isLikedByUser = false;
			
			if(likeList != null) {
				
				for(String likedUser : likeList.split(",")) {
					if(Long.parseLong(likedUser) == currentUserid) {
						isLikedByUser = true;
						break;
					}
				}
			}
			
			
			String userPostList = currentUser.getPostList();
			boolean isCurrentUserPost =false; // eger login olmus user ın postu ise silme butonu gosterilebilir
			
			if(userPostList != null) {
				for(String userPost: userPostList.split(",")) {
					
					if(Long.parseLong(userPost) == post.getPostid()) {
						isCurrentUserPost = true;
					}
				}
			}
			
			long userid =post.getUserid();
			User user = userRepository.findByUserid(userid);
			
			postJson.put("username",user.getUsername());
			postJson.put("displayName", user.getDisplayName());
			postJson.put("userImage", user.getImage());
			postJson.put("isCurrentUserPost", isCurrentUserPost);
			postJson.put("isLikedByUser", isLikedByUser);
			postJson.put("postid", post.getPostid());
			postJson.put("userid", post.getUserid());
			postJson.put("subClubid", post.getSubclubid());
			postJson.put("image", post.getImage());
			postJson.put("content", post.getContent());
			postJson.put("likeCount", post.getLikeCount());
			postJson.put("timestamp", post.getTimestamp());
			posts.add(postJson);
		}
		
		return posts;
		
		
		}
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("{userid}/getuserposts")
	public JSONArray getOwnPosts(@PathVariable long userid, @CurrentUser User currentUser) {
		
		long currentUserid = currentUser.getUserid();
		
		JSONArray posts = new JSONArray(); 
		
		if(userid == currentUserid ) {
			
			List<Post> postPage =  postRepository.findByUseridOrderByTimestamp(userid);
			
			for(Post post: postPage) {
				
				JSONObject postJson = new JSONObject();
				
				String likeList = post.getLikeList();
				boolean isLikedByUser = false;
				
				if(likeList != null) {
					
					for(String likedUser : likeList.split(",")) {
						if(Long.parseLong(likedUser) == currentUserid) {
							isLikedByUser = true;
							break;
						}
					}
				}
				
				postJson.put("username",currentUser.getUsername());
				postJson.put("displayName", currentUser.getDisplayName());
				postJson.put("userImage", currentUser.getImage());
				postJson.put("isCurrentUserPost", true);
				postJson.put("isLikedByUser", isLikedByUser);
				postJson.put("postid", post.getPostid());
				postJson.put("userid", post.getUserid());
				postJson.put("subClubid", post.getSubclubid());
				postJson.put("image", post.getImage());
				postJson.put("content", post.getContent());
				postJson.put("likeCount", post.getLikeCount());
				postJson.put("timestamp", post.getTimestamp());
				posts.add(postJson);
			}
			
			
			
		}
		
		else {
			
			User user = userRepository.findByUserid(userid);
			
			String userSubClubList = user.getSubClubList();
	        String currentUserSubClubList = currentUser.getSubClubList();
	        
	        ArrayList<Long> lst1 = new ArrayList<Long>();
	        ArrayList<Long> lst2 = new ArrayList<Long>();
	        ArrayList<Long> commonSubClubIdList = new ArrayList<Long>();
	        
	        if(userSubClubList != null && currentUserSubClubList!=null) {
	        	
	        	if(userSubClubList.equals("")== false && currentUserSubClubList.equals("") == false) {
	        		
	        		for (String id1 : userSubClubList.split(",")) {

	                    lst1.add(Long.parseLong(id1));

	                }

	                for (String id2 : currentUserSubClubList.split(",")) {

	                    lst2.add(Long.parseLong(id2));

	                }

	                for (long idInList1 : lst1) {

	                    if (lst2.contains(idInList1)) {
	                        commonSubClubIdList.add(idInList1);
	                    }
	                }
	        		
	        	}
	        	
	        }
	        
	        List<Post> commonPosts = new ArrayList<Post>();
	        
	        if(commonSubClubIdList.size() > 0) {
	        	List<Post> userPosts = postRepository.findByUserid(userid);
	        	
	        	if(userPosts!=null) {
	        		
	        		if(userPosts.size() > 0) {
	        			
	        			for(Post userPost: userPosts) {
	        				
	        				if(commonSubClubIdList.contains(userPost.getSubclubid())== true) {
	        					
	        					commonPosts.add(userPost);
	        				}	        				
	        			}
	        		}	        		
	        	}	        	
	        }
	        
	        
	        if(commonPosts.size()>0 ) {
	        	
	    		Collections.sort(commonPosts, new Comparator<Post>() {
	                @Override public int compare(Post p1, Post p2) {
	                    return p2.getTimestamp().compareTo(p1.getTimestamp());
	                } });
	        	
	        }
	        
	        for(Post commonPost: commonPosts) {
	        	
				
				JSONObject commonPostJson = new JSONObject();
				
				String commonPostlikeList = commonPost.getLikeList();
				boolean isLikedByUserCommonPost = false;
				
				if(commonPostlikeList != null) {
					
					for(String commonPostlikedUser : commonPostlikeList.split(",")) {
						if(Long.parseLong(commonPostlikedUser) == currentUserid) {
							isLikedByUserCommonPost = true;
							break;
						}
					}
				}
				
				commonPostJson.put("username",user.getUsername());
				commonPostJson.put("displayName", user.getDisplayName());
				commonPostJson.put("isCurrentUserPost", false);
				commonPostJson.put("isLikedByUser", isLikedByUserCommonPost);
				commonPostJson.put("postid", commonPost.getPostid());
				commonPostJson.put("userid", commonPost.getUserid());
				commonPostJson.put("subClubid", commonPost.getSubclubid());
				commonPostJson.put("image", commonPost.getImage());
				commonPostJson.put("content", commonPost.getContent());
				commonPostJson.put("likeCount", commonPost.getLikeCount());
				posts.add(commonPostJson);
	        	
	        	
	        }
	        
	        
		}
			
		return posts;
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	@GetMapping("/getpostshomepage")
	public JSONArray getPostsHomePage(@CurrentUser User currentUser ) {
		
		long currentUserid = currentUser.getUserid();
		
		String userSubClubList =currentUser.getSubClubList();
		
		JSONArray posts = new JSONArray();

		ArrayList<Post> totalPosts = new ArrayList<Post>();
		
		if(userSubClubList != null) {
			
			for(String subClubid: userSubClubList.split(",")) {
				String getSubClubID = subClubid.split("-")[0];
				getSubClubID=getSubClubID.substring(1, getSubClubID.length());
				List<Post> subClubPosts =postRepository.findBySubclubid(Long.parseLong(getSubClubID));
				totalPosts.addAll(subClubPosts);
			}
			
		}
		
		if(totalPosts.size() > 0) {
		
		Collections.sort(totalPosts, new Comparator<Post>() {
            @Override public int compare(Post p1, Post p2) {
                return p2.getTimestamp().compareTo(p1.getTimestamp());
            } });
		
		for(Post post: totalPosts) {
			
			JSONObject postJson = new JSONObject();
			
			String likeList = post.getLikeList();
			boolean isLikedByUser = false;
			
			if(likeList != null) {
				
				for(String likedUser : likeList.split(",")) {
					if(Long.parseLong(likedUser) == currentUserid) {
						isLikedByUser = true;
						break;
					}
				}
			}
			
			
			String userPostList = currentUser.getPostList();
			boolean isCurrentUserPost =false; // eger login olmus user ın postu ise silme butonu gosterilebilir
			
			if(userPostList != null) {
				for(String userPost: userPostList.split(",")) {
					
					if(Long.parseLong(userPost) == post.getPostid()) {
						isCurrentUserPost = true;
					}
				}
			}
			
			long userid =post.getUserid();
			User user = userRepository.findByUserid(userid);
			
			postJson.put("username",user.getUsername());
			postJson.put("displayName", user.getDisplayName());
			postJson.put("userImage", user.getImage());
			postJson.put("isCurrentUserPost", isCurrentUserPost);
			postJson.put("isLikedByUser", isLikedByUser);
			postJson.put("postid", post.getPostid());
			postJson.put("userid", post.getUserid());
			postJson.put("subClubid", post.getSubclubid());
			postJson.put("image", post.getImage());
			postJson.put("content", post.getContent());
			postJson.put("likeCount", post.getLikeCount());
			postJson.put("timestamp", post.getTimestamp());
			posts.add(postJson);
		}
		}
		return posts;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@GetMapping("/getlikedposts")
	public JSONArray getLikedPosts(@CurrentUser User currentUser) {
		
		
		String likeList =currentUser.getLikeList();
		JSONArray posts = new JSONArray();
		long currentUserid= currentUser.getUserid();
		
		List<Post> postPage = new ArrayList<Post>();
		
		if(likeList == null) {
			return null;
		}
		
		ArrayList<Long> likedpostids = new ArrayList<Long>();
		
		for(String likedPost : likeList.split(",")) {
			likedpostids.add(Long.parseLong(likedPost));
		}
		Collections.reverse(likedpostids);
		
		for(int i = 0 ; i<likedpostids.size();i++) {
			
			postPage.add(postRepository.findByPostid(likedpostids.get(i)));
		}
		
		for(Post post: postPage) {
			
			JSONObject postJson = new JSONObject();
			
			String postlikeList = post.getLikeList();
			boolean isLikedByUser = false;
			
			if(postlikeList != null) {
				
				for(String likedUser : postlikeList.split(",")) {
					if(Long.parseLong(likedUser) == currentUserid) {
						isLikedByUser = true;
						break;
					}
				}
			}
			User user = userRepository.findByUserid(post.getUserid());
			
			postJson.put("username",user.getUsername());
			postJson.put("displayName", user.getDisplayName());
			postJson.put("userImage", user.getImage());
			postJson.put("isCurrentUserPost", false);
			postJson.put("isLikedByUser", isLikedByUser);
			postJson.put("postid", post.getPostid());
			postJson.put("userid", post.getUserid());
			postJson.put("subClubid", post.getSubclubid());
			postJson.put("image", post.getImage());
			postJson.put("content", post.getContent());
			postJson.put("likeCount", post.getLikeCount());
			postJson.put("timestamp", post.getTimestamp());
			posts.add(postJson);
		
		}
		
		return posts;
		
	}
	
	
	@SuppressWarnings("unchecked")
	@GetMapping("/getpost/{postid}")
	public JSONObject getPost(@PathVariable long postid, @CurrentUser User currentUser) {
		
		Post post = postRepository.findByPostid(postid);
		
		JSONObject postJson = new JSONObject();
		
		if(post== null) {
			postJson.put("error", "Post could not be found");
			return postJson;
		}
		
		SubClub subClub =subClubRepository.findBysubClubid(post.getSubclubid());
		
		if(subClub == null) {
			postJson.put("error", "Post could not be found");
			return postJson;
		}
		
		String subClubUserList =subClub.getUserList();
		
		boolean isSubClubMember = false;
		
		if(subClubUserList !=null) {
		
			for(String subClubUser :subClubUserList.split(",")) {
				if(currentUser.getUserid() == Long.parseLong(subClubUser)) {
					isSubClubMember = true;
				}
			}
		
		}
		
		if(isSubClubMember == false) {
			postJson.put("error", "You are not allowed to see this post");
			return postJson;
		}
		
		
		// Add liked Users to json
		
		String likeList = post.getLikeList();
		JSONArray postLikeList = new JSONArray();
		
		if(likeList== null) {
			postJson.put("likeList", null);
		}
		else {
			for(String likedUserid : likeList.split(",")) {
				User likedUser = userRepository.findByUserid(Long.parseLong(likedUserid));
				 
				if(likedUser!=null) {
					JSONObject likedUserJson = new JSONObject();
					likedUserJson.put("username",likedUser.getUsername());
					likedUserJson.put("displayName", likedUser.getDisplayName());
					postLikeList.add( likedUserJson);
				}
			}
			postJson.put("likeList", postLikeList);
		}
		
		String commentList = post.getCommentList();
		JSONArray postCommentList = new JSONArray();
		
		if(commentList == null) {
			postJson.put("commentList", null);
		}
		else {
			
			for(String commentid: commentList.split(",")) {
				Comment comment =commentRepository.findByCommentid(Long.parseLong(commentid));
				if(comment !=null) {
					JSONObject commentJson = new JSONObject();
					long userid =comment.getUserid();
					User user = userRepository.findByUserid(userid);
					commentJson.put("username", user.getUsername());
					commentJson.put("userid", userid);
					commentJson.put("image", user.getImage());
					commentJson.put("content", comment.getContent());
					commentJson.put("commentid", comment.getCommentid());
					commentJson.put("timeStamp", comment.getTimestamp());
					postCommentList.add(commentJson);
				}
			}
			postJson.put("commentList", postCommentList );
		}
		
		long userid =post.getUserid();
		User user = userRepository.findByUserid(userid);
		
		postJson.put("username",user.getUsername());
		postJson.put("displayName", user.getDisplayName());
		postJson.put("postid", post.getPostid());
		postJson.put("userid", post.getUserid());
		postJson.put("subClubid", post.getSubclubid());
		postJson.put("image", post.getImage());
		postJson.put("content", post.getContent());
		postJson.put("likeCount", post.getLikeCount());
		postJson.put("timeStamp",post.getTimestamp());
		
		
		boolean isLikedByUser = false;
		
		if(likeList != null) {
			
			for(String likedUser : likeList.split(",")) {
				if(Long.parseLong(likedUser) == currentUser.getUserid()) {
					isLikedByUser = true;
					break;
				}
			}
		}
		
		String userPostList = currentUser.getPostList();
		boolean isCurrentUserPost =false; // eger login olmus user ın postu ise silme butonu gosterilebilir
		
		if(userPostList != null) {
			for(String userPost: userPostList.split(",")) {
				
				if(Long.parseLong(userPost) == post.getPostid()) {
					isCurrentUserPost = true;
				}
			}
		}
		
		postJson.put("isLikedByUser", isLikedByUser);
		postJson.put("isCurrentUserPost", isCurrentUserPost);
		
		return postJson;
	}
	
	@PutMapping("/likepost")
	public GenericResponse likePost(@RequestBody JSONObject json, @CurrentUser User currentUser) {
		System.out.println(json);
		long postid = Long.valueOf((Integer) json.get("postid"));
		
		Post post =postRepository.findByPostid(postid);
		
		long userid = currentUser.getUserid();
		
		boolean isPostLiked;
		
		if(post == null) {
			return new  GenericResponse("Error: An error occured");
		}
		
		String userLikeList =currentUser.getLikeList();
		String postLikeList = post.getLikeList();
		
		if(userLikeList == null) {
			isPostLiked = false;
		}
		
		else {
			isPostLiked = false;
			for(String userLikedPost :userLikeList.split(",")) {
				
				if (Long.parseLong(userLikedPost) == postid ) {
					
					isPostLiked = true;
				}
				
			}
		}
		
		
		if(isPostLiked==false) {
			
			if(userLikeList == null) {
				userLikeList = Long.toString(postid);
				
			}
			
			else {
				userLikeList = userLikeList + "," + Long.toString(postid);
			}
			
			if(postLikeList == null) {
				
				postLikeList = Long.toString(userid);
			}
			else {
				postLikeList = postLikeList + "," + Long.toString(userid);
			}
			
			post.setLikeCount(post.getLikeCount()+1);
			currentUser.setLikeList(userLikeList);
			post.setLikeList(postLikeList);
			
			postRepository.save(post);
			userRepository.save(currentUser);
			return new GenericResponse("Post Liked");
		}
		
		else {
			
			
		
			currentUser.setLikeList(removeFromStringList(userLikeList, Long.toString(postid)));
			post.setLikeList(removeFromStringList(postLikeList, Long.toString(userid)));
			post.setLikeCount(post.getLikeCount()-1); 
			
			postRepository.save(post);
			userRepository.save(currentUser);
			
			return new GenericResponse("Post unliked");
			
		}
		
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
