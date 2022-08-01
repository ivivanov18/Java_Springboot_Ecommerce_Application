package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import javax.validation.ReportAsSingleViolation;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private final UserRepository userRepository;
	
	private final CartRepository cartRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public UserController(UserRepository userRepo, CartRepository cartRepo, BCryptPasswordEncoder encoder) {
		this.userRepository = userRepo;
		this.cartRepository = cartRepo;
		this.bCryptPasswordEncoder = encoder;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		logger.info("username set with: {}", createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if (createUserRequest.getPassword() == null) {
			logger.error("User {} cannot be created. Password is null.", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		if (createUserRequest.getConfirmPassword() == null) {
			logger.error("User {} cannot be created. Password confirmation is null.", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		if (createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())
		) {
			logger.error("User {} cannot be created. Password length or confirmation do not match the requirements.", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		logger.info("User {} has been created.", user.getUsername());
		return ResponseEntity.ok(user);
	}

}
