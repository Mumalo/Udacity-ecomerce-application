package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}

	private Logger log = LoggerFactory.getLogger(UserController.class);
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null){
			log.error("User not found");
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try {
			User user = new User();
			user.setUsername(createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			if (createUserRequest.getPassword().length() < 7 ||
					!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
				log.error("Failed to create user..invalid user info");
				return ResponseEntity.badRequest().build();
			}
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			userRepository.save(user);
			log.info("successfully saved user");
			return ResponseEntity.ok(user);
		} catch (Exception ex){
			log.error("Failed to create user with error" + ex.getLocalizedMessage());
			return ResponseEntity.badRequest().build();
		}
	}
	
}
