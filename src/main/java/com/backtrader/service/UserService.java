package com.backtrader.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backtrader.exception.NoValuePresentException;
import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.model.UserModel;
import com.backtrader.repository.ForgotPasswordTokenRepository;
import com.backtrader.repository.MarginRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.requestresponse.ForgotPassword;
import com.backtrader.requestresponse.ResetPassword;
import com.backtrader.userentity.ForgotPasswordToken;
import com.backtrader.userentity.Margin;
import com.backtrader.userentity.Role;
import com.backtrader.userentity.User;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
	Logger logger = LoggerFactory.getLogger(UserService.class);
	ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
	@Value("$(spring.mail.username)")
	private String username;
	Date date = null;
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private ForgotPasswordTokenRepository forgotPasswordTokenRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MarginRepository marginRepository;

	public UserService(UserRepository userRepository, ForgotPasswordTokenRepository forgotPasswordTokenRepository) {
		this.userRepository = userRepository;
		this.forgotPasswordTokenRepository = forgotPasswordTokenRepository;
	}

	@Autowired
	@Lazy
	private JwtToken jwtToken;

	public ResponseEntity<String> createUser(UserModel userModel) {

		// CHECKING NULL VALUES FOR THE MODEL IF NULL OBJECT RECEIVED THROW ERROR
		// MESSAGE
		if (userModel == null) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Please enter valid details");
		}

		// CHECKING THE USER FROM THE DATABASE IF THE USER DOES NOT EXIST THEN CREATE
		// NEW USER
		User userdetails = userRepository.findByEmail(userModel.getEmail());
		if (userdetails == null) {

			// SETTING USER DATA FROM USERMODEL TO THE USER ENTITY
			User user = new User();
			user.setFirstname(userModel.getFirstname());
			user.setLastname(userModel.getLastname());
			user.setEmail(userModel.getEmail());
			user.setPassword(encoder.encode(userModel.getPassword()));
			user.setPhone(userModel.getPhone());

			// SETTINGUP DEFAULT ROLE AS USER FOR THE USER
			Role roles = new Role();
			roles.setRoleName("USER");
			List<Role> role = new ArrayList<>();
			role.add(roles);
			user.setRole(role);

			// SETTING UP THE MARGIN FOR THE USER WITH 1 CRORE AS A DEFAULT MARGIN
			Margin margin = new Margin();
			margin.setMarginAvailable(10000000);
			margin.setOpeningBalance(10000000);
			margin.setDate(new Date(System.currentTimeMillis()));
			margin.setUser(user);

			try {
				// CREATING A MAIL MESSAGE FOR SENDING THE RESET LINK
				SimpleMailMessage mailMessage = new SimpleMailMessage();
				mailMessage.setFrom(username);
				mailMessage.setTo(userModel.getEmail());
				mailMessage.setSubject("Registration successful");
				mailMessage.setText(("Congratulations " + userModel.getFirstname()
						+ "\n Your account has been created for the trading App"));
				javaMailSender.send(mailMessage);
				userRepository.save(user);
				marginRepository.save(margin);
			} catch (Exception e) {
				logger.error("There is an issue with the application. Please try after sometime {} ", e.getMessage());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("There is an issue with the application. Please try after sometime.");
			}

			logger.info("User has been created with user name {} {}", user.getFirstname(), user.getLastname());

			// GIVE THE SUCCESS RESPONSE WITH STATUS CODE CREATED
			return ResponseEntity.status(HttpStatus.CREATED).body("You have been signed up");

		}
		// IF THE USER EXISTE RETURN THE ERROR MESSAGE FOR THE USER ALREADY EXIST
		else {
			return ResponseEntity.status(HttpStatus.FOUND).body("User Already Exist");
		}
	}

	public ResponseEntity<String> createAdmin(UserModel userModel, String token) {

		logger.info("create admin method started with token {}", token);

		// EXTRACTING THE TOKEN FROM THE BEARER TOKEN
		String extractedToken = token.substring(7);
		String email = jwtToken.getUserNameFromToken(extractedToken);
		User returnedUser = userRepository.findByEmail(email);

		logger.info("User details has been fetched {}", returnedUser);
		List<Role> userRole = returnedUser.getRole();
		try {
			// CHECKING WHETHER THE USER HAS ROLE ADMIN OR NOT
			Role userroles = userRole.stream().filter(role -> role.getRoleName().equals("ADMIN")).findFirst()
					.orElseThrow(() -> new NoValuePresentException("User has no access to register to the admin"));
			String userrole = userroles.getRoleName();

			// CHECKING WHETHER THE LOGGED IN PERSON IS ADMIN OR NOT
			if (userrole.equals("ADMIN")) {
				User userdetails = userRepository.findByEmail(userModel.getEmail());
				if (userdetails == null) {

					// USER DETAILS ARE GETTING FROM THE USERMODEL AND STOREING IT INTO USER ENTITY
					// TO STORE IT IN DATABASE
					User user = new User();
					user.setFirstname(userModel.getFirstname());
					user.setLastname(userModel.getLastname());
					user.setEmail(userModel.getEmail());
					user.setPassword(encoder.encode(userModel.getPassword()));
					user.setPhone(userModel.getPhone());

					// SETTING UP THE ROLE AS A ADMIN TO THE ADMIN CREATION METHO
					Role roles = new Role();
					roles.setRoleName("ADMIN");
					List<Role> role = new ArrayList<>();
					role.add(roles);
					user.setRole(role);
					userRepository.save(user);

					logger.info("Admin has been created with the admin name {}", user.getFirstname());

					try {
						// CREATING A MAIL MESSAGE FOR SENDING THE RESET LINK
						SimpleMailMessage mailMessage = new SimpleMailMessage();
						mailMessage.setFrom(username);
						mailMessage.setTo(userModel.getEmail());
						mailMessage.setSubject("Registration successful");
						mailMessage.setText(("Congratulations " + userModel.getFirstname()
								+ "\n Your account has been created for the trading App"));
						javaMailSender.send(mailMessage);
						userRepository.save(user);
					} catch (Exception e) {
						logger.error("There is an issue with the application. Please try after sometime {} ",
								e.getMessage());
						return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body("There is an issue with the application. Please try after sometime.");
					}

					// RETURN THE CREATED RESPONSE WITH SUCESS MESSAGE
					return ResponseEntity.status(HttpStatus.CREATED).body("You have been signed up");

				} else {

					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Already Exist");
				}
			}
		} catch (NoValuePresentException ex) {
			logger.info("exception occured at admin registration {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			logger.info("User Does not exist");
			// THROWS AN EXCPETION IF THE USE DOES NOT EXIST
			throw new UsernameNotFoundException("Unable to fetch user details");

		} else {

			// RETURNING THE USER DETAILS FOR THE USER WHICH IS AVAILABLE IN THE DATABASE
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
					user.getRole());
		}
	}

	public String forgotPasswordTokenGenarate(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			// RETURN THE ERROR MESSAGE IF USER DOES NOT FIND IN THE DATABASE
			return "User does not exist";
		}
		// CREAT UUID TOKEN AND RETURN IT SO THAT IN THE RESET METHOD WE CAN SEND IT
		// WITH URL
		String token = UUID.randomUUID().toString();
		forgotPasswordToken.setToken(token);
		forgotPasswordToken.setUser(user);
		forgotPasswordToken.setTokenCreatedTime(new Date(System.currentTimeMillis() + 3600000).getTime());

		// SAVE THE TOKEN INTO DATABASE AND RETURN IT
		forgotPasswordTokenRepository.save(forgotPasswordToken);
		return token;

	}

	public ResponseEntity<String> forgotPassword(ForgotPassword forgotPassword) {
		User users = userRepository.findByEmail(forgotPassword.getEmail());
		if (users == null) {
			// IF USER DOES NOT FIND IN THE DATABASE RETURN THE ERROR MESSAGE WITH BAD
			// REQUEST
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("User Doesnot exist.. Please enter the valid email.");

		}
		// CALL THE GENERATE TOKEN METHOD HERE TO GET THE TOKEN
		String token = this.forgotPasswordTokenGenarate(forgotPassword.getEmail());

		// CREATE A MAIL MESSAGE TO SEND THE EMAIL WITH THE RESET LINK
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(username);
			mailMessage.setTo(forgotPassword.getEmail());
			mailMessage.setSubject("Reset password link");

			// ADDING THE TOKEN TO THE URL FROM THE GENERATE TOKEN METHOD
			mailMessage.setText("Click on the below link to reset the password \n"
					+ "http://localhost:3000/reset-password?token=" + token);

			// SENDING EMAIL TO THE EMAIL OF AN USER
			javaMailSender.send(mailMessage);
		} catch (Exception e) {
			logger.error("There is an issue with the application please try after sometime {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("There is an issue with the application please try after sometime");
		}
		date = new Date(System.currentTimeMillis());

		// RETURN THE SUCCESS MESSAGE WITH THE RESPONSE CODE ACCEPT
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body("Email has been sent with reset link. reset your password within 1 hour");

	}

	public ResponseEntity<String> resetPassword(String token, ResetPassword resetPassword) {
		// CHECK IF THE TOKEN IS STORED IN THE DATABASE OR NOT
		ForgotPasswordToken passwordToken = forgotPasswordTokenRepository.findByToken(token);

		if (passwordToken == null) {
			// IF TOKEN DOES NOT EXIST IN THE DATABASE RETURN THE ERROR MESSAGE WITH NOT
			// FOUND STATUS CODE
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found");
		}

		Date currentTime = new Date(System.currentTimeMillis());

		// IF THE TOKEN AVAILABLE CHECK WHETHER THE CURRENT TIME IS GREATER THAN THE
		// TOKEN GENERATED TIME
		// IF IT IS GREAT THEN WE CAN PROCEED FOR THE RESET PASSWORD
		if (passwordToken.getTokenCreatedTime() >= (currentTime).getTime()) {
			forgotPasswordTokenRepository.delete(passwordToken);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("The link might be expired, please regenerate the link");
		}

		User user = passwordToken.getUser();

		if (user == null) {
			// IF USER NOT FOUND THEN DELETE THE CREATED TOKEN FROM THE DATABASE
			forgotPasswordTokenRepository.delete(passwordToken);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}

		if (!resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword())) {
			// IF THE NEW PASSWORD AND THE CONFIROMED PASSWORD ARE NOT SAME THEN RETURN THE
			// ERROR CODE WITH ERROR MESSAGE
			return ResponseEntity.status(HttpStatus.CONFLICT).body("New password and confirm password do not match");
		}

		if (passwordToken.getToken().equals(token)) {
			// IF THE TOKENS ARE SAME PROCEED FOR THE PASSWORD RESET AND SET THE NEW
			// PASSWORD AND SAVE IT TO THE DATABASE
			user.setPassword(encoder.encode(resetPassword.getNewPassword()));
			userRepository.save(user);

			// ONCE THE DATA IS SAVED YOU CAN DELETE THE TOKEN FROM THE DATABASE
			forgotPasswordTokenRepository.delete(passwordToken);
			return ResponseEntity.status(HttpStatus.OK).body("Password has been changed");
		}

		forgotPasswordTokenRepository.delete(passwordToken);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
	}

	public User findByEmail(String email) {
		// FINDING THE EMAIL FROM THE DATABASE USING REPOSITORY
		return userRepository.findByEmail(email);
	}

}
