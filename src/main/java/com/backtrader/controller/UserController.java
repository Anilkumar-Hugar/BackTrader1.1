package com.backtrader.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.model.UserModel;
import com.backtrader.requestresponse.AuthRequest;
import com.backtrader.requestresponse.AuthResponse;
import com.backtrader.requestresponse.ForgotPassword;
import com.backtrader.requestresponse.ResetPassword;
import com.backtrader.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.Setter;

@RestController
@Setter
public class UserController {
	Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtToken jwtToken;

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
		AuthResponse response = null;

		// AUTHENTICATING THE USER BASED ON THE GIVEN USERNAME AND PASSWORD
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		if (authentication == null) {
			// IF USER DOES NOT EXIST THEN RETURING THE ERROR MESSAGE
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		/*
		 * IF AUTHNETICATION IS NOT NULL THEN FINDING THE USERDETAILS USING EMAIL AND
		 * RETURNING RESPONSE WITH JWT TOKEN
		 */
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		// GENERATING THE JWT TOKEN FOR THE SECURITY
		String token = jwtToken.generateJwtToken(userDetails.getUsername());
		List<String> roles = userDetails.getAuthorities().stream().map(list -> list.getAuthority()).toList();
		response = new AuthResponse(jwtToken.getUserNameFromToken(token), roles, token);
		logger.info("User has been logged in with the user email {}", userDetails.getUsername());

		// RETURNING THE RESPONSE STATUS OK WITH THE TOKEN OBJECT
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping(value = "/signup", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createUser(@RequestBody @Valid UserModel user) {
		/*
		 * HERE CALLING THE SERVICE METHOD FOR THE CREATE USER AND SENDING THE RESPONSE
		 * CODE ACCORDINGLY BY CHECKING ALL THE CONDITIONS
		 */
		return userService.createUser(user);

	}

	@PostMapping(value = "/adminsignup", consumes = { MediaType.APPLICATION_JSON_VALUE })
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<String> createAdmin(@RequestBody @Valid UserModel userModel,
			@RequestHeader(name = "Authorization") String token) {
		/*
		 * HERE CALLING THE SERVICE METHOD FOR THE CREATE ADMIN AND SENDING THE RESPONSE
		 * CODE ACCORDINGLY BY CHECKING ALL THE CONDITIONS
		 */
		return userService.createAdmin(userModel, token);
	}

	@PostMapping(value = "/forgot-passsword")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPassword forgotPassword) throws MessagingException {

		/*
		 * HERE CALLING THE SERVICE METHOD FOR THE FORGOT PASSWORD AND SENDING THE
		 * RESPONSE CODE ACCORDINGLY BY CHECKING ALL THE CONDITIONS
		 */
		return userService.forgotPassword(forgotPassword);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody ResetPassword resetPassword) {

		/*
		 * TAKING THE PASSWORD AND RESETING THE PASSWORD BY CALLING THE SERVICE METHOD
		 * TO RESET THE PASSWORD
		 */
		return userService.resetPassword(token, resetPassword);
	}

}
