package com.backtrader.adminusertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.model.UserModel;
import com.backtrader.repository.UserRepository;
import com.backtrader.service.UserService;
import com.backtrader.userentity.Roles;
import com.backtrader.userentity.Users;

class AdminServiceTest {
	Logger logger = mock(Logger.class);
	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtToken jwtTokenUtil;

	@InjectMocks
	private UserService adminService;
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		bCryptPasswordEncoder = new BCryptPasswordEncoder();

	}

	@Test
	void testCreateAdmin_Successful() {
		// Mocking the JWT token and user
		String token = "Bearer valid_token";
		String email = "user@example.com";
		UserModel userModel = new UserModel("John", "Doe", "john.doe@example.com", 1234567890L, "password123");
		Users user = new Users();
		user.setEmail(email);
		user.setRoles(Collections.singletonList(new Roles(1, "ADMIN")));
		if (user.getRoles().get(0).getRole().equals("ADMIN")) {
			when(jwtTokenUtil.getUserNameFromToken(any())).thenReturn(email);
			when(userRepository.findByEmail((email))).thenReturn(user);

			// Mocking the UserRepository save method
			when(userRepository.findByEmail((userModel.getEmail()))).thenReturn(null);
			when(userRepository.save(any())).thenReturn(new Users());

			ResponseEntity<String> response = adminService.createAdmin(userModel, token);
			assertEquals(HttpStatus.CREATED, response.getStatusCode());
			assertEquals("You have been signed up", response.getBody());
		} else {
			when(adminService.createAdmin(userModel, token)).thenReturn(null);
		}
	}

	@Test
	void testCreateAdmin_UserAlreadyExists() {
		// Mocking the JWT token and user
		String token = "Bearer valid_token";
		String email = "user@example.com";
		UserModel userModel = new UserModel("John", "Doe", "john.doe@example.com", 1234567890L, "password123");
		Users user = new Users();
		user.setEmail(email);
		user.setRoles(Collections.singletonList(new Roles(1, "ADMIN")));
		when(jwtTokenUtil.getUserNameFromToken(any())).thenReturn(email);
		when(userRepository.findByEmail((email))).thenReturn(user);

		// Mocking the UserRepository findByEmail to return an existing user
		when(userRepository.findByEmail((userModel.getEmail()))).thenReturn(new Users());

		ResponseEntity<String> response = adminService.createAdmin(userModel, token);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals("User Already Exist", response.getBody());
	}

	@Test
	void testCreateAdmin_NonAdminUserToken() {
		// Mocking the JWT token and user
		String token = "Bearer valid_token";
		String email = "user@example.com";
		UserModel userModel = new UserModel("John", "Doe", "john.doe@example.com", 1234567890L, "password123");
		Users user = new Users();
		user.setEmail(email);
		user.setRoles(Collections.singletonList(new Roles(1, "USER"))); // Non-admin user role
		when(jwtTokenUtil.getUserNameFromToken(any())).thenReturn(email);
		when(userRepository.findByEmail((email))).thenReturn(user);
		ResponseEntity<String> response = adminService.createAdmin(userModel, token);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertEquals("User has no access to register to the admin", response.getBody());
	}
}
