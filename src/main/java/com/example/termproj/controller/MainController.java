package com.example.termproj.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.example.termproj.model.Team;
import com.example.termproj.model.User;
import com.example.termproj.repository.TeamRepository;
import com.example.termproj.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@SessionAttributes("user")
public class MainController {
	
	@Autowired
	private TeamRepository tr;
	
	@Autowired
	private UserRepository ur;
	
	//Using PoJo Classes
	@GetMapping("/teams")
	
	public ModelAndView getTeams(HttpSession session) {
		ModelAndView showTeams = new ModelAndView("showTeams");
		showTeams.addObject("name", session.getAttribute("userName")); 
		
		//Endpoint to call
		String url ="https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json";
		//Encode Username and Password
        String encoding = Base64.getEncoder().encodeToString("2ca22ce5-3d2f-4776-a118-7c2680:termproject123".getBytes());
        // TOKEN:PASS
        //Add headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic "+encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		//Make the call
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<NBATeamStanding> response = restTemplate.exchange(url, HttpMethod.GET, request, NBATeamStanding.class);
		NBATeamStanding ts = response.getBody(); 
        System.out.println(ts.toString());
		//Send the object to view
        showTeams.addObject("teamStandingEntries", ts.getOverallteamstandings().getTeamstandingsentries());
        
		return showTeams;
	}
	
	@ModelAttribute("user")
	public User setUpUserForm()
	{
		return new User();
	}
	
	//Using objectMapper
	@GetMapping("/team")
	public ModelAndView getTeamInfo(
			@RequestParam("id") String teamID 
			) {
		ModelAndView teamInfo = new ModelAndView("teamInfo");
		ArrayList<HashMap<String, String>> gameDetails = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/team_gamelogs.json?team=" + teamID;
		String encoding = Base64.getEncoder().encodeToString("2ca22ce5-3d2f-4776-a118-7c2680:termproject123".getBytes());
        
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic "+encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody(); 
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
		
	        System.out.println(root.get("teamgamelogs").get("lastUpdatedOn").asText());
	        System.out.println(root.get("teamgamelogs").get("gamelogs").getNodeType());
	        JsonNode gamelogs = root.get("teamgamelogs").get("gamelogs");
	       
	        HashMap<String, String> gml= new HashMap<String, String>();
	        
	        JsonNode t1=gamelogs.get(1).get("team"); 
	        gml.put("name", t1.get("Name").asText());
	        gml.put("abb", t1.get("Abbreviation").asText());
	        gml.put("id", t1.get("ID").asText());
	        System.out.println("inside teamProf Check");
	        teamInfo.addObject("teamProf", gml);
	        
	        if(gamelogs.isArray()) {
	        	
	        	gamelogs.forEach(gamelog -> {
	        		JsonNode game = gamelog.get("game");
	        		HashMap<String,String> gameDetail = new HashMap<String, String>();
	        		gameDetail.put("id", game.get("id").asText());
	        		gameDetail.put("date", game.get("date").asText());
	        		gameDetail.put("time", game.get("time").asText());
	        		gameDetail.put("wins", gamelog.get("stats").get("Wins").get("#text").asText());
	        		gameDetail.put("losses", gamelog.get("stats").get("Losses").get("#text").asText());
	        		gameDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());
	        		gameDetails.add(gameDetail);
	        		
	        	});
	        }
		} catch (IOException e) {

			e.printStackTrace();
		}

		teamInfo.addObject("gameDetails", gameDetails);
		
        
		return teamInfo;
	}
	
	
	@PostMapping(value = "/favadded")
	public ModelAndView HandleProfile(@RequestParam("id") String teamID, @RequestParam("abb") String teamAbb,
			@RequestParam("name") String teamName,HttpSession session,@ModelAttribute User user) {
		ModelAndView teamProfile = new ModelAndView();
		
		Team tm=new Team();
		System.out.println("j##########3"+teamID);
		User tg = null;
		String idname = session.getAttribute("userId").toString();
		Long id = Long.parseLong(idname);
		tg = ur.findById(id);
		System.out.println("session" + tg.getId());
		long tyy = tg.getId();
		System.out.println("long" + tyy);
		List<Team> team1 = tr.findAll();

		List<String> teamdata=new ArrayList<>();

		for(Team t:team1) {
			if(t.getUser_id().compareTo(id)==0) {
				teamdata.add(t.getName());
			}
		}

		
		if (tg.getId() == null) {

			System.out.println("before redirection");
			ModelAndView rt = new ModelAndView("redirect:/login");
			return rt;
		}
		
		if(teamdata.contains(teamName)) {
			
		}else {
		tm.setName(teamName);
		tm.setTeam_id(Integer.parseInt(teamID));
		tm.setAbbreviation(teamAbb);
		
	
		tm.setUser_id(id);
		tr.save(tm);
		
		
		}
		List<Team> teamdata1=new ArrayList<>();

		for(Team t:teamdata1) {
			if(t.getUser_id().compareTo(id)==0) {
				teamdata1.add(t);
			}
		}

		
		teamProfile.addObject("teamProf", teamdata1);
		teamProfile.setViewName("redirect:/home");
		return teamProfile;
	
	}
	
	@GetMapping("/score")
	public ModelAndView getScoreInfo() {
		ModelAndView scoreInfo = new ModelAndView("scoreInfo");
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
	    String dtf= DateTimeFormatter.ofPattern("yyyyMMdd").format(yesterday);
	    System.out.println(dtf);
		ArrayList<HashMap<String, String>> scoreBoard = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/scoreboard.json?fordate="+dtf;
		String encoding = Base64.getEncoder().encodeToString("2ca22ce5-3d2f-4776-a118-7c2680:termproject123".getBytes());
        
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic "+encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody(); 
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
			//JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
	        System.out.println(root.get("scoreboard").get("lastUpdatedOn").asText());
	        System.out.println(root.get("scoreboard").get("gameScore").getNodeType());
	        JsonNode gameScore1 = root.get("scoreboard").get("gameScore");
	        
	        if(gameScore1.isArray()) {
	        	
	        	gameScore1.forEach(gamescore -> {
	        		JsonNode game = gamescore.get("game");
	        		
	        		HashMap<String,String>scoreDetail = new HashMap<String, String>();
	        		scoreDetail.put("id", game.get("ID").asText());
	        		scoreDetail.put("date", game.get("date").asText());
	        		scoreDetail.put("time", game.get("time").asText());
	        		scoreDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());
	        		scoreDetail.put("homeTeam", game.get("homeTeam").get("Abbreviation").asText());
	        		scoreDetail.put("awayScore", gamescore.get("awayScore").asText());
	        		scoreDetail.put("homeScore", gamescore.get("homeScore").asText());
	        		
	        		scoreBoard.add(scoreDetail);
	        		
	        	});
	        }
	        
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
		scoreInfo.addObject("scoreDetails", scoreBoard);
		
        
		return scoreInfo;
	}
	
	

	
	
	@GetMapping("/rank")
	public ModelAndView getRankInfo(
			
			) {
		ModelAndView rankInfo = new ModelAndView("rankInfo");
		ArrayList<HashMap<String, String>> rankDetails = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json"; 
		//+ teamID;
		String encoding = Base64.getEncoder().encodeToString("2ca22ce5-3d2f-4776-a118-7c2680:termproject123".getBytes());
        
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic "+encoding);
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
	        
	        if(teamstandingsentry.isArray()) {
	        	
	        	teamstandingsentry.forEach(gamelog -> {
	        		JsonNode game = gamelog.get("team");
	        		HashMap<String,String> gameDetail = new HashMap<String, String>();
	        		gameDetail.put("name", game.get("Name").asText());
	        		gameDetail.put("abb", game.get("Abbreviation").asText());
	        		gameDetail.put("rank", gamelog.get("rank").asText());
	        		
	        		rankDetails.add(gameDetail);
	        		
	        	});
	        }
		} catch (IOException e) {

			e.printStackTrace();
		}
	 
		rankInfo.addObject("rankDetails", rankDetails);
		
        
		return rankInfo;
	}	
}