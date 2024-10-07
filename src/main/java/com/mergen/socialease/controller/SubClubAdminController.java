package com.mergen.socialease.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mergen.socialease.model.SubClub;
import com.mergen.socialease.model.SubClubAdminRequest;
import com.mergen.socialease.model.User;
import com.mergen.socialease.model.Club;
import com.mergen.socialease.model.ClubRequest;
import com.mergen.socialease.model.Comment;
import com.mergen.socialease.model.Post;
import com.mergen.socialease.model.Report;
import com.mergen.socialease.repository.SubClubRepository;
import com.mergen.socialease.repository.ClubRepository;
import com.mergen.socialease.repository.ClubRequestRepository;
import com.mergen.socialease.repository.CommentRepository;
import com.mergen.socialease.repository.ConfirmationTokenRepository;
import com.mergen.socialease.repository.ForgotTokenRepository;
import com.mergen.socialease.repository.PostRepository;
import com.mergen.socialease.repository.ReportRepository;
import com.mergen.socialease.repository.SubClubAdminRequestRepository;
import com.mergen.socialease.repository.UserRepository;
import com.mergen.socialease.service.EmailSenderService;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;

@RestController
public class SubClubAdminController {

	@Autowired
	private SubClubRepository subClubRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReportRepository reportRepository;
	

	@Autowired
	private ClubRepository clubRepository;

	@Autowired
	private ClubRequestRepository cRRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SubClubAdminRequestRepository sCARRepository;

	@PostMapping("/report")
	public GenericResponse report(@RequestBody Report r, @CurrentUser User user) {
		if (r.getReportType() != 1) {
			return new GenericResponse("Error: Type is wrong!");
		}
		if (r.getReported() == null || r.getReported().equals("") || r.getReporter() == null
				|| r.getReporter().equals("")) {
			return new GenericResponse("Error: Something is wrong!");
		}
		if (r.getExplanation() == null || r.getExplanation() == "") {
			return new GenericResponse("Error: invalid-explanation");
		}
		try {
			User u = userRepository.findByUsername(r.getReported());
			String sCString = u.getSubClubList();
			String[] scList = sCString.split(",");
			Long SCID = r.getSubClubid();
			boolean check1 = true;
			for (int i = 0; i < scList.length; i++) {
				String x = scList[i].split("-")[0];
				x=x.substring(1, x.length());
				if (SCID == Long.parseLong(x)) {
					check1 = false;
					break;
				}
			}
			if (check1) {
				return new GenericResponse("Error: This user is not member of this subclub!");
			}
		} catch (Exception e) {
			System.out.println(e);
			return new GenericResponse("Error: Something is wrong!");
		}
		reportRepository.save(r);
		return new GenericResponse("Report is sent!");
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/viewreport")
	public ArrayList viewReport(@RequestBody JSONObject reportTypee, @CurrentUser User user) {

		Long reportType = Long.valueOf((Integer) reportTypee.get("reportType"));
		if (user.getIsSubClubAdmin() == -1 || user.getIsSubClubAdmin() == -2 || user.getIsSubClubAdmin() == -3) {
			ArrayList<String> list = new ArrayList<String>();
			list.add("You are not a subclub admin");
			return list;
		} else
			return reportRepository.findAllByReportTypeAndSubClubid(reportType, user.getIsSubClubAdmin());

	}
	
	
	@PostMapping("/evaluatereport")
	public GenericResponse evalueateReport(@RequestBody JSONObject json, @CurrentUser User user) {
		if(user.getIsSubClubAdmin()>=0) {
			Long reportid = Long.valueOf((Integer) json.get("reportid"));
			boolean ban = (boolean) json.get("ban");
			Report r = reportRepository.findByreportid(reportid);
			String x = r.getReported();
			User reportedUser = userRepository.findByUsername(x);
			SubClub subclub = subClubRepository.findBysubClubid(user.getIsSubClubAdmin());
			String[] list1=subclub.getUserList().split(",");
			List<String> targetList = Arrays.asList(list1);
			if(targetList.contains(Long.toString(reportedUser.getUserid()))) {
				if (ban == false) {
					reportRepository.deleteById(reportid);
					return new GenericResponse("Report Deleted");
				} else {
					reportRepository.deleteById(reportid);
					try {
						String[] postList = reportedUser.getPostList().split(",");
						for (int i = 0; i < postList.length; i++) {
							long postid = Long.parseLong(postList[i]);
							Post deletedPost = postRepository.findByPostid(postid);

							if (deletedPost == null) {
								return new GenericResponse("Error: Post could not be found");
							}

							if (reportedUser.getUserid() != deletedPost.getUserid()) {
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

							User user2 = userRepository.findByUserid(deletedPost.getUserid());

							String userPostList = user2.getPostList();

							user2.setPostList(removeFromStringList(userPostList, Long.toString(postid)));

							// Update subclub's post list

							SubClub subClub = subClubRepository.findBysubClubid(deletedPost.getSubclubid());

							String subClubPostList = subClub.getPostList();

							subClub.setPostList(removeFromStringList(subClubPostList, Long.toString(postid)));

							user2.setTotalPostCount(user2.getTotalPostCount() - 1);

							subClubRepository.save(subClub);
							userRepository.save(user2);
							postRepository.delete(deletedPost);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						String[] comList = reportedUser.getCommentList().split(",");
						for (int i = 0; i < comList.length; i++) {
							commentRepository.deleteById(Long.parseLong(comList[i]));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						for (ClubRequest creq : cRRepository.findAll()) {
							if (creq.getUserid() == reportedUser.getUserid()) {
								cRRepository.deleteById(creq.getClubRequestid());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						for (Report report : reportRepository.findAll()) {
							if (userRepository.findByUsername(report.getReporter()).getUserid() == reportedUser.getUserid()) {
								reportRepository.deleteById(report.getReportid());
							}
							if (userRepository.findByUsername(report.getReported()).getUserid() == reportedUser.getUserid()) {
								reportRepository.deleteById(report.getReportid());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						for (SubClubAdminRequest sCAR : sCARRepository.findAll()) {
							if (sCAR.getUserid() == reportedUser.getUserid()) {
								sCARRepository.deleteById(sCAR.getSubClubAdminRequestid());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if(reportedUser.getIsSubClubAdmin()>=0) {
							SubClub sc = subClubRepository.findBysubClubid(reportedUser.getIsSubClubAdmin());
							sc.setAdminid((long) -1);
							subClubRepository.save(sc);
						}
						String[] subClubList = reportedUser.getSubClubList().split(",");
						for (int i = 0; i < subClubList.length; i++) {
							String indis = subClubList[i].split("-")[0];
							indis = indis.substring(1);
							SubClub subClub = subClubRepository.findBysubClubid(Long.parseLong(indis));
							String[] userList = subClub.getUserList().split(",");
							List<String> userList2 = new ArrayList<String>(Arrays.asList(userList));
							userList2.remove(Long.toString(reportedUser.getUserid()));
							String str = String.join(",", userList2);
							subClub.setUserList(str);
							subClubRepository.save(subClub);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						String[] clubList = reportedUser.getClubList().split(",");
						for (int i = 0; i < clubList.length; i++) {
							Club club = clubRepository.findByclubid(Long.parseLong(clubList[i]));
							String[] userList = club.getUserList().split(",");
							List<String> userList2 = new ArrayList<String>(Arrays.asList(userList));
							userList2.remove(Long.toString(reportedUser.getUserid()));
							String str = String.join(",", userList2);
							club.setUserList(str);
							clubRepository.save(club);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					userRepository.delete(reportedUser);
					return new GenericResponse("User banned (Actually, banned from Socialease :) )");
				}
			}
			else {
				return new GenericResponse("ERROR");
			}
		}
		else {
			return new GenericResponse("ERROR");
		}
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/getnameofthesubclub")
	public JSONObject getNameOfTheSubClub(@RequestParam Long id, @CurrentUser User user) {
		JSONObject json = new JSONObject();
		try {
			json.put("nameOfSubClub", subClubRepository.findBysubClubid(id).getName());
		}
		catch(Exception e) {}
		return json;
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
