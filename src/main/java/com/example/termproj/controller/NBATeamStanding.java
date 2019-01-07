package com.example.termproj.controller;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

class team{
	@JsonProperty("ID")
	String ID;
	@JsonProperty("City")
	String City;
	@JsonProperty("Name")
	String Name;
	@JsonProperty("Abbreviation")
	String Abbreviation;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getAbbreviation() {
		return Abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		Abbreviation = abbreviation;
	}
}

class teamstandingsentry{
	team team;
	Long rank;
	public team getTeam() {
		return team;
	}
	public void setTeam(team team) {
		this.team = team;
	}
	public Long getRank() {
		return rank;
	}
	public void setRank(Long rank) {
		this.rank = rank;
	}	
	
}

class overallteamstandings{
	String lastUpdatedOn;
	@JsonProperty("teamstandingsentry")
	ArrayList<teamstandingsentry> teamstandingsentries;
	
	
	public ArrayList<teamstandingsentry> getTeamstandingsentries() {
		return teamstandingsentries;
	}
	public void setTeamstandingsentries(ArrayList<teamstandingsentry> teamstandingsentries) {
		this.teamstandingsentries = teamstandingsentries;
	}
	
}

@JsonIgnoreProperties(ignoreUnknown = true)
public class NBATeamStanding {
	overallteamstandings overallteamstandings;

	public overallteamstandings getOverallteamstandings() {
		return overallteamstandings;
	}

	public void setOverallteamstandings(overallteamstandings overallteamstandings) {
		this.overallteamstandings = overallteamstandings;
	}
	
}