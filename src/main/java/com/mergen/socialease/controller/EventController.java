package com.mergen.socialease.controller;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mergen.socialease.model.Event;
import com.mergen.socialease.model.SubClub;
import com.mergen.socialease.model.User;
import com.mergen.socialease.repository.EventRepository;
import com.mergen.socialease.repository.SubClubRepository;
import com.mergen.socialease.shared.CurrentUser;
import com.mergen.socialease.shared.GenericResponse;

@RestController
public class EventController {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private SubClubRepository subClubRepository;

	@PostMapping("/createevent")
	public GenericResponse createEvent(@RequestBody JSONObject event, @CurrentUser User currentUser) {

		long subClubid = Long.valueOf((Integer) event.get("subClubid"));
		String title = (String) event.get("title");
		String content = (String) event.get("content");

		if (subClubid != currentUser.getIsSubClubAdmin()) {
			return new GenericResponse("Error: You must be subclub admin to create an event");
		}

		if (title == null || content == null) {
			return new GenericResponse("Error: You cannot leave event details empty");
		}

		if (title.equals("") == true || content.equals("") == true) {
			return new GenericResponse("Error: You cannot leave event details empty");
		}

		Event newEvent = new Event();
		newEvent.setContent(content);
		newEvent.setTitle(title);
		newEvent.setSubclubid(subClubid);

		eventRepository.save(newEvent);

		long eventid = newEvent.getEventid();

		SubClub subClub = subClubRepository.findBysubClubid(subClubid);
		String subClubEventList = subClub.getEventList();

		if (subClubEventList == null) {
			subClubEventList = Long.toString(eventid);
		} else {
			subClubEventList = subClubEventList + "," + Long.toString(eventid);
		}

		subClub.setEventList(subClubEventList);
		subClubRepository.save(subClub);

		return new GenericResponse("Event successfully created.");
	}

	@PostMapping("/deleteevent")
	public GenericResponse deleteEvent(@RequestBody JSONObject id, @CurrentUser User currentUser) {

		long eventid = Long.valueOf((Integer) id.get("eventid"));

		Event event = eventRepository.findByEventid(eventid);

		long subClubid = event.getSubclubid();

		if (subClubid != currentUser.getIsSubClubAdmin()) {
			return new GenericResponse("You are not allowed to delete this event");
		}

		SubClub subClub = subClubRepository.findBysubClubid(subClubid);

		String subClubEventList = subClub.getEventList();

		try {
			eventRepository.deleteById(eventid);
			subClub.setEventList(removeFromStringList(subClubEventList, Long.toString(eventid)));
			subClubRepository.save(subClub);
		}

		catch (Exception e) {

			return new GenericResponse("Event could not be deleted");

		}

		return new GenericResponse("Event is successfully deleted");

	}

	@SuppressWarnings("unchecked")
	@PostMapping("/getsubclubevents")

	private JSONArray getSubClubEvents(@RequestBody JSONObject id, @CurrentUser User currentUser) {

		JSONArray events = new JSONArray();

		long subclubid = Long.valueOf((Integer) id.get("subclubid"));

		String userSubClubList = currentUser.getSubClubList();

		boolean isSubClubMember = false;

		if (userSubClubList != null) {

			if (userSubClubList.equals("") == false) {

				for (String usersubClubid : userSubClubList.split(",")) {
					String biktimLan = usersubClubid.split("-")[0];
					biktimLan = biktimLan.substring(1);
					if (Long.parseLong(biktimLan) == subclubid) {

						isSubClubMember = true;
						break;
					}
				}
			}
		}

		if (isSubClubMember == true) {

			List<Event> subClubEventList = eventRepository.findBySubclubid(subclubid);

			for (Event event : subClubEventList) {

				JSONObject eventJSON = new JSONObject();

				eventJSON.put("eventid", event.getEventid());
				eventJSON.put("subclubid", event.getSubclubid());
				eventJSON.put("content", event.getContent());
				eventJSON.put("title", event.getTitle());
				events.add(eventJSON);

			}
		}

		return events;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/getuserevents")
	public JSONArray getUserEvents(@RequestBody JSONObject json, @CurrentUser User currentUser) {

		long userid = Long.valueOf((Integer) json.get("userid"));

		JSONArray userEvents = new JSONArray();

		if (userid == currentUser.getUserid()) {

			String currentUserSubClubList = currentUser.getSubClubList();
			@SuppressWarnings("unused")
			String currentUserUserName = currentUser.getUsername();

			if (currentUserSubClubList != null) {

				if (currentUserSubClubList.equals("") == false) {

					for (String userSubClubid : currentUserSubClubList.split(",")) {
						String biktimLan = userSubClubid.split("-")[0];
						biktimLan = biktimLan.substring(1);
						SubClub subClub = subClubRepository.findBysubClubid(Long.parseLong(biktimLan));

						List<Event> subClubEvents = eventRepository.findBySubclubid(Long.parseLong(biktimLan));

						for (Event subClubEvent : subClubEvents) {

							JSONObject eventJson = new JSONObject();
							eventJson.put("subclubid", subClub.getSubClubid());
							eventJson.put("subClubName", subClub.getName());
							eventJson.put("eventid", subClubEvent.getEventid());
							eventJson.put("title", subClubEvent.getTitle());
							eventJson.put("content", subClubEvent.getContent());
							userEvents.add(eventJson);
						}

					}

				}
			}

		}

		return userEvents;

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
