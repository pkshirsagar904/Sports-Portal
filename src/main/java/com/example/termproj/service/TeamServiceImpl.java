package com.example.termproj.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.termproj.model.*;
import com.example.termproj.repository.*;
import com.example.termproj.service.*;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {
	
@Autowired
private TeamRepository teamRepository;

	@Override
	public Team createTeam(Team team) {
		// TODO Auto-generated method stub
		return teamRepository.save(team);
	}

	@Override
	public Team getTeam(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Team editTeam(Team team) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTeam(Team team) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTeam(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Team> getAllTeams(int TeamNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Team> getAllTeams() {
		// TODO Auto-generated method stub
		System.out.println("getting the teams");
		return (List<Team> )teamRepository.findAll();
	}

}
