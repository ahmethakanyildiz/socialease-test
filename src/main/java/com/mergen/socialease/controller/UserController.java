package com.mergen.socialease.controller;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.mergen.socialease.model.ConfirmationToken;
import com.mergen.socialease.model.User;
import com.mergen.socialease.model.Creds;
import com.mergen.socialease.model.Club;
import com.mergen.socialease.model.ClubRequest;
import com.mergen.socialease.model.Comment;
import com.mergen.socialease.model.SubClub;
import com.mergen.socialease.model.SubClubAdminRequest;
import com.mergen.socialease.model.ForgotToken;
import com.mergen.socialease.model.Post;
import com.mergen.socialease.model.Report;
import com.mergen.socialease.service.EmailSenderService;
import com.mergen.socialease.repository.ConfirmationTokenRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.repository.ClubRepository;
import com.mergen.socialease.repository.ClubRequestRepository;
import com.mergen.socialease.repository.CommentRepository;
import com.mergen.socialease.repository.SubClubRepository;
import com.mergen.socialease.repository.ForgotTokenRepository;
import com.mergen.socialease.repository.PostRepository;
import com.mergen.socialease.repository.ReportRepository;
import com.mergen.socialease.repository.SubClubAdminRequestRepository;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;
import com.mergen.socialease.shared.Views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@RestController
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClubRepository clubRepository;

	@Autowired
	private SubClubRepository subClubRepository;

	@Autowired
	private ClubRequestRepository cRRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private SubClubAdminRequestRepository sCARRepository;

	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private ForgotTokenRepository forgotTokenRepository;

	@Autowired
	private EmailSenderService emailSenderService;

	PasswordEncoder passEncoder = new BCryptPasswordEncoder();

	@PostMapping("/register")
	public GenericResponse registerUser(@Valid @RequestBody User user) {
		user.setPassword(passEncoder.encode(user.getPassword()));
		user.setRegistered(false);
		user.setSurveyAnswered(false);
		user.setIsThereNewClub(false);
		user.setIsSubClubAdmin(-1);
		user.setBiographi("Write about yourself!");
		user.setTotalPostCount(0);
		userRepository.save(user);

		ConfirmationToken confirmationToken = new ConfirmationToken(user);

		confirmationTokenRepository.save(confirmationToken);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("chand312902@gmail.com");
		mailMessage.setText("To confirm your account, please click here : " + "http://localhost:3000/#/confirm?token="
				+ confirmationToken.getConfirmationToken());

		emailSenderService.sendEmail(mailMessage);

		return new GenericResponse(user.getEmail() + " is added!");
	}

	@PostMapping("/auth")
	@JsonView(Views.Base.class)
	public ResponseEntity<?> login(@CurrentUser User user) {
		return ResponseEntity.ok(user);
	}

	@RequestMapping(value = "/confirm", method = { RequestMethod.GET, RequestMethod.POST })
	public GenericResponse confirmUserAccount(@RequestParam("token") String confirmationToken) {
		System.out.println(confirmationToken);
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		User user = userRepository.findByEmail(token.getUser().getEmail());
		user.setRegistered(true);
		userRepository.save(user);
		confirmationTokenRepository.delete(token);
		return new GenericResponse("Activation is successful! You can login with your username and password.");
	}

	@PostMapping("/forgot")
	public GenericResponse forgot(@RequestBody JSONObject json) {
		String email = (String) json.get("email");
		User u = userRepository.findByEmail(email);
		if (u == null)
			return new GenericResponse("Error: Something is wrong!");

		ForgotToken forgotToken = new ForgotToken(u);
		forgotTokenRepository.save(forgotToken);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(u.getEmail());
		mailMessage.setSubject("Password Recovery");
		mailMessage.setFrom("chand312902@gmail.com");
		mailMessage.setText("http://localhost:3000/#/recover?token=" + forgotToken.getForgotToken());
		emailSenderService.sendEmail(mailMessage);
		return new GenericResponse("Please check your e-mail");
	}

	@PostMapping("/recover")
	public GenericResponse recover(@RequestParam String token, @Valid @RequestBody Creds creds) {

		ForgotToken forgotToken = forgotTokenRepository.findByforgotToken(token);
		Long userid = forgotToken.getUser().getUserid();
		User u = userRepository.findByUserid(userid);
		if (u == null)
			throw new Error();
		String newPassword = creds.getPassword();
		u.setPassword(passEncoder.encode(newPassword));
		userRepository.save(u);
		forgotTokenRepository.delete(forgotToken);
		return new GenericResponse("Password Changed");
	}

	// EĞER USER O CLUB'A VEYA SUBCLUB'A ÜYE DEĞİLSE, HATA VERECEK!
	@SuppressWarnings("unchecked")
	@GetMapping("/getuserwithmode")
	public JSONArray getUser(@RequestParam Long mode, Long id, @CurrentUser User user) {
		if (id < 0)
			throw new Error();
		if (mode == 1) {
			if (!isUserMember(user.getClubList(), id)) {
				JSONArray Json = new JSONArray();
				Json.add("Error: This user is not a member of this club.");
				return Json;
			}
			return getClubUsers(id);
		} else if (mode == 2) {
			if (!isUserMember(user.getSubClubList(), id)) {
				JSONArray Json = new JSONArray();
				Json.add("Error: This user is not a member of this subclub.");
				return Json;
			}
			return getSubClubUsers(id);
		} else
			throw new Error();
	}

	private boolean isUserMember(String list, long id) {
		if (list == null)
			return false;
		for (String st : list.split(",")) {
			String s = st.split("-")[0];
			Integer ID;
			if (s.charAt(0) == '[')
				ID = Integer.parseInt(s.substring(1));
			else
				ID = Integer.parseInt(s);

			if (ID == id)
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getSingleUser(Long userId) {
		User user = userRepository.findByUserid(userId);
		JSONObject userJson = new JSONObject();

		userJson.put("id", user.getUserid());
		userJson.put("username", user.getUsername());

		return userJson;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getClubUsers(Long clubId) {
		JSONArray clubUsers = new JSONArray();
		Club c = clubRepository.findByclubid(clubId);

		try {
			for (String s : c.getUserList().split(","))
				clubUsers.add(getSingleUser(Long.parseLong(s)));
		} catch (Exception e) {

		}

		return clubUsers;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getSubClubUsers(Long subClubId) {

		JSONArray subClubUsers = new JSONArray();
		SubClub sc = subClubRepository.findBysubClubid(subClubId);

		try {
			for (String s : sc.getUserList().split(","))
				subClubUsers.add(getSingleUser(Long.parseLong(s)));
		} catch (Exception e) {

		}

		return subClubUsers;

	}

	// EĞER CURRENT USER İLE, ARADIĞI USER'IN ORTAK HİÇBİR KULÜBÜ YOKSA HATA
	// VERECEK!
	@SuppressWarnings("unchecked")
	@PostMapping("/getuserdetails")
	public JSONObject getUserDetails(@RequestBody JSONObject usernameee, @CurrentUser User currentUser) {
		String username = (String) usernameee.get("username");
		JSONObject userJson = new JSONObject();

		User user1 = userRepository.findByUsername(username);

		if (!checkCommonClubs(user1, currentUser)) {
			userJson.put("error", "Error: Users do not have any clubs in common");
			return userJson;
		}

		String uname = username;
		if (uname.charAt(uname.length() - 1) == '=') {
			uname = uname.substring(0, uname.length() - 1);
		}
		User u = userRepository.findByUsername(uname);

		if (u == null) {
			userJson.put("error", "Error: There is no user with this username!");
			return userJson;
		}

		userJson.put("userId", u.getUserid());
		userJson.put("username", u.getUsername());
		userJson.put("displayName", u.getDisplayName());
		userJson.put("email", u.getEmail());
		userJson.put("isSCAdmin", u.getIsSubClubAdmin());
		userJson.put("image", u.getImage());
		userJson.put("isThereNewClub", u.isThereNewClub());
		userJson.put("biographi", u.getBiographi());
		userJson.put("postCount", u.getTotalPostCount());
		JSONArray clubJson = new JSONArray();
		if (u.getClubList() == null || u.getClubList().equals("")) {
			clubJson = null;
		} else {
			for (String s : u.getClubList().split(",")) {
				Club c = clubRepository.findByclubid(Long.parseLong(s));

				JSONObject newClub = new JSONObject();
				newClub.put("clubId", c.getClubid());
				newClub.put("clubName", c.getClubName());
				clubJson.add(newClub);
			}
		}

		JSONArray subClubJson = new JSONArray();
		if (u.getSubClubList() == null || u.getSubClubList().equals("")) {
			subClubJson = null;
		} else {
			for (String s : u.getSubClubList().split(",")) {
				String s2 = s.substring(1, s.length() - 1);
				String[] temp = s2.split("-");
				SubClub subclub = subClubRepository.findBysubClubid(Long.parseLong(temp[0]));

				JSONObject subclubjsonobject = new JSONObject();
				subclubjsonobject.put("subClubid", subclub.getSubClubid());
				subclubjsonobject.put("subClubName", subclub.getName());
				subclubjsonobject.put("admin", subclub.getAdminid());
				subclubjsonobject.put("clubId", subclub.getClubid());
				subClubJson.add(subclubjsonobject);
			}
		}

		userJson.put("clubs", clubJson);
		userJson.put("subclubs", subClubJson);
		userJson.put("error", "NO");
		return userJson;
	}

	private boolean checkCommonClubs(User user1, User user2) {

		if (user1.getClubList() == null || user2.getClubList() == null)
			return false;

		for (String st : user1.getClubList().split(","))
			if (isUserMember(user2.getClubList(), Long.parseLong(st)))
				return true;

		return false;
	}

	@PostMapping("/saveuserimage")
	public GenericResponse saveUserImage(@RequestBody JSONObject json, @CurrentUser User user) {
		long userid = Long.valueOf((Integer) json.get("id"));
		if (userid != user.getUserid()) {
			return new GenericResponse("Error: you are a hacker");
		} else {
			try {
				String base64Image = (String) json.get("file");
				String dataType = base64Image.split(";")[0];
				if (!("data:image/png".equals(dataType) || "data:image/jpeg".equals(dataType)
						|| "data:image/jpg".equals(dataType))) {
					return new GenericResponse("Error: Image format can be jpg, jpeg or png!");
				} else {
					user.setImage(base64Image);
					userRepository.save(user);
					return new GenericResponse("Profile picture updated");
				}
			} catch (Exception e) {
				return new GenericResponse("Error: Something is wrong!");
			}
		}
	}

	@PostMapping("/deleteuserimage")
	public GenericResponse deleteUserImage(@CurrentUser User user) {
		if (user.getImage() == null) {
			return new GenericResponse("Warning: you don't have a profile picture already!");
		} else {
			user.setImage(null);
			userRepository.save(user);
			return new GenericResponse("Profile picture deleted");
		}
	}

	// @RequestBody String newDisplayName,@PathVariable String username,
	// @CurrentUser User loggedInUser
	@PostMapping("/updateuser/{usernamee}")
	@PreAuthorize("#usernamee==#user.username")
	public GenericResponse updateUserProfile(@RequestBody JSONObject json, @PathVariable String usernamee,
			@CurrentUser User user) {
		String username = (String) json.get("username");
		String displayname = (String) json.get("displayname");
		String biographi = (String) json.get("biographi");
		String password = (String) json.get("password");

		Boolean dncontrol = true;
		Boolean uncontrol = true;
		Boolean bcontrol = true;
		Boolean pcontrol = true;

		// changes checked

		if (displayname == null || displayname.equals("") || displayname.equals(user.getDisplayName())) {
			dncontrol = false;
		}
		if (username == null || username.equals("") || username.equals(user.getUsername())) {
			uncontrol = false;
		}
		if (biographi == null || biographi.equals("") || biographi.equals(user.getBiographi())) {
			bcontrol = false;
		}
		if (password == null || password.equals("") || user.getPassword().equals(passEncoder.encode(password))) {
			pcontrol = false;
		}

		if (dncontrol == false && uncontrol == false && bcontrol == false && pcontrol == false) {
			return new GenericResponse("Warning: No changes were detected");
		}

		// display name
		if (dncontrol == true && displayname.length() < 5) {
			return new GenericResponse(
					"Warning: Invalid display name. Display Name must contain at least 5 characters");
		} else if (dncontrol == true && displayname.length() > 255) {
			return new GenericResponse(
					"Warning: Invalid display name. Display Name must contain  maximum 255 characters");
		} else if (dncontrol == true) {
			user.setDisplayName(displayname);
		}

		// biographi
		if (bcontrol == true && biographi.length() < 5) {
			return new GenericResponse("Warning: Invalid biographi. Biographi must contain at least 5 characters");
		} else if (bcontrol == true && biographi.length() > 255) {
			return new GenericResponse("Warning: Invalid biographi. Biographi must contain  maximum 255 characters");
		} else if (bcontrol == true) {
			user.setBiographi(biographi);
		}

		// username
		if (uncontrol == true && username.length() < 5) {
			return new GenericResponse("Warning: Invalid username. Username must contain at least 5 characters");
		} else if (uncontrol == true && username.length() > 255) {
			return new GenericResponse("Warning: Invalid username. Username must contain  maximum 255 characters");
		}
		if (uncontrol == true && !user.getUsername().equals(username)) {
			for (User u : userRepository.findAll()) {
				String uname = u.getUsername();
				if (username.equals(uname))
					return new GenericResponse("Warning: Username already exists");
			}
		}

		String regexusername = "[a-zA-Z\\.0-9]*";
		if (uncontrol == true && !Pattern.matches(regexusername, username))
			return new GenericResponse(
					"Warning: Username can only contain uppercase letters, lowercase letters, numbers or '.' character.");
		if (uncontrol == true) {
			user.setUsername(username);
		}

		// password

		if (pcontrol == true && password.length() < 5) {
			return new GenericResponse("Warning: Invalid password. Password must contain at least 5 characters");
		} else if (pcontrol == true && password.length() > 255) {
			return new GenericResponse("Warning: Invalid password. Password must contain  maximum 255 characters");
		}

		String regexpassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$";

		if (pcontrol == true && !Pattern.matches(regexpassword, password))
			return new GenericResponse(
					"Warning: The password must contain at least one uppercase letter, one lowercase letter and one number.");

		String regexpassword2 = "[^:\\s]*";

		if (pcontrol == true && !Pattern.matches(regexpassword2, password))
			return new GenericResponse("Password must not contain space and ':' characters.");

		if (pcontrol == true) {
			user.setPassword((passEncoder.encode(password)));
		}

		userRepository.save(user);

		return new GenericResponse("Success: Profile updated");
	}

	@PostMapping("/deleteuser")
	public GenericResponse deleteUser(@CurrentUser User currentUser) {

		try {
			String[] postList = currentUser.getPostList().split(",");
			for (int i = 0; i < postList.length; i++) {
				long postid = Long.parseLong(postList[i]);
				Post deletedPost = postRepository.findByPostid(postid);

				if (deletedPost == null) {
					return new GenericResponse("Error: Post could not be found");
				}

				if (currentUser.getUserid() != deletedPost.getUserid()) {
					return new GenericResponse("Error: You are not allowed to delete this post");
				}

				// Delete Comments of the post

				String commentList = deletedPost.getCommentList();

				if (commentList != null) {

					for (String commentid : commentList.split(",")) {

						Comment deletedComment = commentRepository.findByCommentid(Long.valueOf(commentid));
						User commenterUser = userRepository.findByUserid(deletedComment.getUserid());
						String userCommentList = commenterUser.getCommentList();

						commenterUser.setCommentList(removeFromStringList(userCommentList, commentid));
						userRepository.save(commenterUser);
						commentRepository.deleteById(Long.parseLong(commentid));

					}

				}

				// Userların like Listini güncelle

				String likeList = deletedPost.getLikeList();

				if (likeList != null) {

					for (String likeruserid : likeList.split(",")) {
						User likerUser = userRepository.findByUserid(Long.parseLong(likeruserid));
						String userLikeList = likerUser.getLikeList();

						likerUser.setLikeList(removeFromStringList(userLikeList, Long.toString(postid)));
						userRepository.save(likerUser);
					}

				}

				// Userdan postu sil

				User user = userRepository.findByUserid(deletedPost.getUserid());

				String userPostList = user.getPostList();

				user.setPostList(removeFromStringList(userPostList, Long.toString(postid)));

				// Update subclub's post list

				SubClub subClub = subClubRepository.findBysubClubid(deletedPost.getSubclubid());

				String subClubPostList = subClub.getPostList();

				subClub.setPostList(removeFromStringList(subClubPostList, Long.toString(postid)));

				user.setTotalPostCount(user.getTotalPostCount() - 1);

				subClubRepository.save(subClub);
				userRepository.save(user);
				postRepository.delete(deletedPost);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String[] comList = currentUser.getCommentList().split(",");
			for (int i = 0; i < comList.length; i++) {
				commentRepository.deleteById(Long.parseLong(comList[i]));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (ClubRequest creq : cRRepository.findAll()) {
				if (creq.getUserid() == currentUser.getUserid()) {
					cRRepository.deleteById(creq.getClubRequestid());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			for (Report report : reportRepository.findAll()) {
				if (userRepository.findByUsername(report.getReporter()).getUserid() == currentUser.getUserid()) {
					reportRepository.deleteById(report.getReportid());
				}
				if (userRepository.findByUsername(report.getReported()).getUserid() == currentUser.getUserid()) {
					reportRepository.deleteById(report.getReportid());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			for (SubClubAdminRequest sCAR : sCARRepository.findAll()) {
				if (sCAR.getUserid() == currentUser.getUserid()) {
					sCARRepository.deleteById(sCAR.getSubClubAdminRequestid());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if(currentUser.getIsSubClubAdmin()>=0) {
				SubClub sc = subClubRepository.findBysubClubid(currentUser.getIsSubClubAdmin());
				sc.setAdminid((long) -1);
				subClubRepository.save(sc);
			}
			String[] subClubList = currentUser.getSubClubList().split(",");
			for (int i = 0; i < subClubList.length; i++) {
				String indis = subClubList[i].split("-")[0];
				indis = indis.substring(1);
				SubClub subClub = subClubRepository.findBysubClubid(Long.parseLong(indis));
				String[] userList = subClub.getUserList().split(",");
				List<String> userList2 = new ArrayList<String>(Arrays.asList(userList));
				userList2.remove(Long.toString(currentUser.getUserid()));
				String str = String.join(",", userList2);
				subClub.setUserList(str);
				subClubRepository.save(subClub);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String[] clubList = currentUser.getClubList().split(",");
			for (int i = 0; i < clubList.length; i++) {
				Club club = clubRepository.findByclubid(Long.parseLong(clubList[i]));
				String[] userList = club.getUserList().split(",");
				List<String> userList2 = new ArrayList<String>(Arrays.asList(userList));
				userList2.remove(Long.toString(currentUser.getUserid()));
				String str = String.join(",", userList2);
				club.setUserList(str);
				clubRepository.save(club);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		userRepository.delete(currentUser);
		return new GenericResponse("Success: Your account is deleted");
	}

	private String removeFromStringList(String strList, String removedid) {

		String[] ids = strList.split(",");

		ArrayList<String> newids = new ArrayList<String>();

		for (int i = 0; i < ids.length; i++) {

			if (Long.parseLong(ids[i]) != Long.parseLong(removedid)) {
				newids.add(ids[i]);
			}

		}

		if (newids.isEmpty()) {
			return null;
		}

		else {

			StringBuilder str = new StringBuilder("");

			for (String newid : newids) {

				str.append(newid).append(",");
			}

			String commaseparatedlist = str.toString();

			if (commaseparatedlist.length() > 0)
				commaseparatedlist = commaseparatedlist.substring(0, commaseparatedlist.length() - 1);

			return commaseparatedlist;
		}
	}
}
