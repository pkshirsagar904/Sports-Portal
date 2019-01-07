package com.example.termproj.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.termproj.model.User;


public interface UserRepository extends CrudRepository<User, Integer> {
	public User findById(Long id);

}
