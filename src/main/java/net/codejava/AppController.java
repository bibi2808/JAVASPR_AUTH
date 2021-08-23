package net.codejava;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {
	
	@Autowired
	private UserService service;

	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup_form";
	}
	
	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		userRepo.save(user);
		
		return "register_success";
	}
	
//	@GetMapping("/users")
//	public String listUsers(Model model) {
//		List<User> listUsers = userRepo.findAll();
//		model.addAttribute("listUsers", listUsers);
//		
//		return "users";
//	}
	
	@GetMapping("/users")
	public String viewHomePage(Model model, @Param("keyword") String keyword) {
		return listByPage(model, 1, "id", "asc", keyword);
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(Model model,
			@PathVariable("pageNum") int currentPage,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword ) {
		
		Page<User> page = service.listAll(currentPage, sortField, sortDir, keyword);
		long totalItems = page.getTotalElements();
		int totalPages = page.getTotalPages();
		
		List<User> listUsers = page.getContent();
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("keyword", keyword);
		
		model.addAttribute("listUsers", listUsers);
		
		return "users";
	}
	
	@PostMapping(value = "/save")
	public String saveUser(@ModelAttribute("user") User user) {
		service.save(user);
		
		return "redirect:/users";
	}
	
	
	@GetMapping("/edit/{id}")
	public ModelAndView showEditUserForm(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("edit_user");
		
		User user = service.get(id);
		mav.addObject("user", user);
		
		return mav;
	}
	
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Long id) {
		service.delete(id);
		
		return "redirect:/";
	}
}
