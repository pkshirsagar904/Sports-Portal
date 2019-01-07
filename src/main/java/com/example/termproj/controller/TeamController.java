package com.example.termproj.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.example.termproj.model.*;
import com.example.termproj.repository.TeamRepository;
import com.example.termproj.repository.UserRepository;
import com.example.termproj.service.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@SessionAttributes("user")
public class TeamController {

	@Autowired
	private TeamService tS;

	@Autowired
	private TeamRepository tr;

	@Autowired
	private UserRepository ur;

	

	@GetMapping("/favoriteteam")
	public ModelAndView getFavTeamInfo(
			HttpSession session
	
	) {
		ModelAndView home = new ModelAndView("FavoriteTeam");
		home.addObject("name",session.getAttribute("userName"));
		ArrayList<HashMap<String, String>> favTeamDetails = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json";
		// + teamID;
		String encoding = Base64.getEncoder()
				.encodeToString("2ca22ce5-3d2f-4776-a118-7c2680:termproject123".getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
			System.out.println(root.get("overallteamstandings").get("teamstandingsentry").asText());
			System.out.println(root.get("overallteamstandings").get("teamstandingsentry").getNodeType());
			JsonNode teamstandingsentry = root.get("overallteamstandings").get("teamstandingsentry");

			if (teamstandingsentry.isArray()) {

				teamstandingsentry.forEach(gamelog -> {
					JsonNode game = gamelog.get("team");
					HashMap<String, String> gameDetail = new HashMap<String, String>();
					gameDetail.put("id", game.get("ID").asText());
					gameDetail.put("name", game.get("Name").asText());
					gameDetail.put("abb", game.get("Abbreviation").asText());

					favTeamDetails.add(gameDetail);

				});
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		home.addObject("favTeamDetails", favTeamDetails);

		return home;
	}

	@GetMapping(value = "/home")
	public String homecontrl(Model model, HttpSession session) {
		String idname = session.getAttribute("userId").toString();
		System.out.println("idname"+idname);
		Long id = Long.parseLong(idname);
		List<Team> teAM = tr.findAll();
		List<Team> favteam = new ArrayList<>();
		for (Team t : teAM) {
			try {
				System.err.println("team id"+t.getUser_id());
			if (t.getUser_id().compareTo(id) == 0) {
				favteam.add(t);
			}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			model.addAttribute("teams", favteam);

		}
		return "home";
	}

	@ModelAttribute("user")
	public User setUpUserForm() {
		return new User();
	}
	
	
	

	@PostMapping(value = "/home")
	public ModelAndView addTeam(Model model, HttpServletRequest request, @RequestParam("testOrder") String data,
			HttpSession session, @ModelAttribute User user) {

		User tg = null;
		String idname = session.getAttribute("userId").toString();
		Long id = Long.parseLong(idname);
		tg = ur.findById(id);
		System.out.println("session" + tg.getId());
		long tyy = tg.getId();
		System.out.println("long" + tyy);
		if (tg.getId() == null) {

			System.out.println("before redirection");
			ModelAndView rt = new ModelAndView("redirect:/login");
			return rt;
		}

		ModelAndView rt = new ModelAndView("home");
		System.out.println(data);
		String teamData[] = null;
		String sepData[] = null;
		teamData = data.split(",");
		List<String> commaSep = new ArrayList<>();
		List<Team> ty = new ArrayList<>();
		List<Team> team1 = tr.findAll();
		List<String> favteam1 = new ArrayList<>();
		for (Team t : team1) {
			if (t.getUser_id().compareTo(tg.getId()) == 0) {
				favteam1.add(t.getName());
			}
		}

		System.out.println("Session" + tg.getUsername());
		System.out.println("I am here");
		if (teamData.length > 1) {
			for (int i = 0; i < teamData.length; i++) {
				commaSep.add((teamData[i]));
			}
			for (int j = 0; j < commaSep.size(); j++) {
				Team t = new Team();
				sepData = commaSep.get(j).split(":");

				if(favteam1.contains((sepData[0]))) {
					continue;	
				}

				System.out.println(sepData[0]);
				t.setName(sepData[0]);
				System.out.println(sepData[1]);
				t.setAbbreviation(sepData[1]);
				System.out.println(Integer.parseInt(sepData[2]));
				t.setTeam_id(Integer.parseInt(sepData[2]));
				t.setUser_id(tg.getId());
				System.out.println("I am here 2");
				System.out.println("dddddddddddddddd" + tg);
				tr.save(t);
				// u.setTeam(ty);
				
			}
			System.out.println("Team" + ty.size());

		} else {
			
			System.out.println("I am4 here");
			Team t = new Team();
			String[] singleTeam = data.split(":");
			
			while(favteam1.contains(singleTeam[0])) {
				continue;
			}
			t.setName(singleTeam[0]);
			System.out.println(singleTeam[0]);
			t.setAbbreviation(singleTeam[1]);
			System.out.println(singleTeam[1]);
			t.setTeam_id(Integer.parseInt(singleTeam[2]));
			System.out.println(Integer.parseInt(singleTeam[2]));

			t.setUser_id(tg.getId());



			tr.save(t);
		}

		
		List<Team> s = new ArrayList<Team>();

		List<Team> teAM = tr.findAll();
		List<Team> favteam = new ArrayList<>();
		for (Team t : teAM) {
			if (t.getUser_id().compareTo(tg.getId()) == 0) {
				favteam.add(t);
			}
		}
		System.out.println("tg" + s.size());
		rt.addObject("teams", favteam);
		System.out.println("asadasd");
		return rt;
	}

}
