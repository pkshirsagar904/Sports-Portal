package com.example.termproj.service;

import java.util.List;

import com.example.termproj.model.*;

public interface TeamService {

Team createTeam(Team team);
Team getTeam(Long id);
Team editTeam(Team team);
void deleteTeam(Team team);
void deleteTeam(Long id);
List<Team> getAllTeams(int TeamNumber);
List<Team> getAllTeams();
}