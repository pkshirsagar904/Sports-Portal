package com.example.termproj.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.termproj.model.User;
import com.example.termproj.repository.UserRepository;



@Service
@Transactional
public class UserService {

	@Autowired
	private final UserRepository userRepository;
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	public User findByUserid(Long userid) {
		return userRepository.findById(userid);
	}
	
	
	public void saveMyUser(User user)
	{
		userRepository.save(user);
	}
}