package com.example.termproj.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.example.termproj.model.User;
import com.example.termproj.repository.TeamRepository;
import com.example.termproj.repository.UserRepository;
import com.example.termproj.service.UserService;

@SessionAttributes("user")
@Controller
public class LoginController {

	@Autowired
	private UserRepository uR;

	@Autowired
	private UserService uS;

	@GetMapping("/login")
	public ModelAndView renderPage() {

		ModelAndView login = new ModelAndView();
		System.out.print("#inRenderpage");
		return login;
	}

	@GetMapping("/adminStatus")
	public ModelAndView renderPage3() {

		ModelAndView adminStatus = new ModelAndView("adminStatus");
		System.out.print("#inRenderpage3");
		return adminStatus;
	}
	
	
	@GetMapping("/")
	public ModelAndView renderPage5(HttpSession session) {

		ModelAndView mv2 = new ModelAndView("index");

		String un=session.getAttribute("userName").toString();
		System.out.println(un);
		mv2.addObject(un);
		System.out.print("#inRenderpage3");
		return mv2;
	}
	

	@ModelAttribute("user")
	public User setUpUserForm() {
		return new User();
	}

	@GetMapping("/blockmsg")
	public ModelAndView renderpg4() {
		ModelAndView mv = new ModelAndView("blockmsg");
		return mv;
	}

	@PostMapping("/login2")
	public ModelAndView handleLogin(@RequestParam("userId") Long id, @RequestParam("userName") String userName,
			HttpSession session, @ModelAttribute User user) {

		System.out.println("i am inside loginHAndle");
		System.out.println("id" + id);
		System.out.println("my userName" + userName);
		session.setAttribute("userId", id);
		session.setAttribute("userName", userName);

		System.out.println(id + userName);
		
		User u = new User();
		User userdata = null;
		List<User> userlist = (List<User>) uR.findAll();
		String presentuser = u.getUsername();
		for (User u11 : userlist) {
			if (u11.getId().compareTo(id) == 0) {
				presentuser = u11.getUsername();
				userdata = u11;
				break;
			}
		}

		if (uR.findById(id) == null) {
			u.setId(id);
			u.setStatus(false);
			u.setUsername(userName);
			u.setRole("User");
			uR.save(u);

		} else {
			if (presentuser.equals("Jacob Alcakhigejai Lisky")) {


				userdata.setRole("Admin");
				uR.save(userdata);

				return new ModelAndView("redirect:/adminStatus");
			}
		}

		if (userdata != null) {
			if (userdata.getStatus()) {

				return new ModelAndView("/blockmsg");

			}
			
		}
		return new ModelAndView("redirect:/");
	}

	@GetMapping("/logout")
	public ModelAndView renderPage1() {

		ModelAndView logout = new ModelAndView();
		System.out.print("#inRenderpage1");
		return logout;
	}

	@GetMapping("/userlist")
	public ModelAndView getUserdetails() {
		ModelAndView nmvv = new ModelAndView("adminInfo");

		List<User> userlist = (List<User>) uR.findAll();
		List<User> userlist1 = new ArrayList<>();

		for (int i = 0; i < userlist.size(); i++) {
			if (userlist.get(i) != null) {
				User u1 = userlist.get(i);
				if (u1.getUsername().equals("Jacob Alcakhigejai Lisky")) {

				} else {
					userlist1.add(userlist.get(i));
				}
			}
		}
		nmvv.addObject("userl", userlist1);
		return nmvv;

	}

	
	@PostMapping("/block")
	public ModelAndView blockinfo(HttpSession session, @RequestParam("id") Long id) {
		ModelAndView nmvv = new ModelAndView("adminInfo");

		List<User> userlist = (List<User>) uR.findAll();
		List<User> userlist1 = new ArrayList<>();

		for (User u : userlist) {
			if (u.getId().compareTo(id) == 0) {
				u.setStatus(true);
				uR.save(u);
				break;
			}
		}

		for (User u : userlist) {
			if (!u.getUsername().equals("Jacob Alcakhigejai Lisky")) {
				if (!u.getStatus()) {

					userlist1.add(u);
				}
			}
		}
		nmvv.addObject("userl", userlist1);
		return nmvv;

	}

}
