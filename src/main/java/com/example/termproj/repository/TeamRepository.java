package com.example.termproj.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.termproj.model.*;

@Repository
public interface TeamRepository extends CrudRepository<Team, Integer> {
	
	
	public List<Team> findAll();
	
}