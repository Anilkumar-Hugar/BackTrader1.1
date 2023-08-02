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
import com.backtrader.repository.UserRepository;
import com.backtrader.requestresponse.ForgotPassword;
import com.backtrader.requestresponse.ResetPassword;
import com.backtrader.userentity.ForgotPasswordToken;
import com.backtrader.userentity.Roles;
import com.backtrader.userentity.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserService implements UserDetailsService {
	Logger logger = LoggerFactory.getLogger(UserService.class);
	ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
	@Value("$(mail.username)")
	private String username;
	Date date = null;
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private ForgotPasswordTokenRepository forgotPasswordTokenRepository;
	@Autowired
	private UserRepository userRepository;

	public UserService(UserRepository userRepository, ForgotPasswordTokenRepository forgotPasswordTokenRepository) {
		this.userRepository = userRepository;
		this.forgotPasswordTokenRepository = forgotPasswordTokenRepository;
	}

	@Autowired
	@Lazy
	private JwtToken jwtToken;

	public ResponseEntity<String> createUser(UserModel userModel) {
		if (userModel != null) {
			Users userdetails = userRepository.findByEmail(userModel.getEmail());
			if (userdetails != null) {

				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Already Exist");
			} else {
				Users users = new Users();
				users.setFirstname(userModel.getFirstname());
				users.setLastname(userModel.getLastname());
				users.setEmail(userModel.getEmail());
				users.setPassword(encoder.encode(userModel.getPassword()));
				users.setPhone(userModel.getPhone());
				Roles roles = new Roles();
				roles.setRole("USER");
				List<Roles> role = new ArrayList<>();
				role.add(roles);
				users.setRoles(role);
				userRepository.save(users);
				logger.info("User has been created with user name {}", users.getFirstname());
				return ResponseEntity.status(HttpStatus.CREATED).body("You have been signed up");

			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Details does not looks correct");
		}
	}

	public ResponseEntity<String> createAdmin(UserModel userModel, String token) {
		logger.info("create admin method started with token {}", token);
		token = token.substring(7);
		String email = jwtToken.getUserNameFromToken(token);
		Users user = userRepository.findByEmail(email);
		logger.info("User details has been fetched {}", user);
		List<Roles> userRole = user.getRoles();
		try {
			Roles userroles = userRole.stream().filter(role -> role.getRole().equals("ADMIN")).findFirst()
					.orElseThrow(() -> new NoValuePresentException("User has no access to register to the admin"));
			String userrole = userroles.getRole();
			if (userrole.equals("ADMIN")) {
				Users userdetails = userRepository.findByEmail(userModel.getEmail());
				if (userdetails != null) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Already Exist");
				} else {
					Users users = new Users();
					users.setFirstname(userModel.getFirstname());
					users.setLastname(userModel.getLastname());
					users.setEmail(userModel.getEmail());
					users.setPassword(encoder.encode(userModel.getPassword()));
					users.setPhone(userModel.getPhone());
					Roles roles = new Roles();
					roles.setRole("ADMIN");
					List<Roles> role = new ArrayList<>();
					role.add(roles);
					users.setRoles(role);
					userRepository.save(users);
					logger.info("Admin has been created with the admin name {}", users.getFirstname());
					return ResponseEntity.status(HttpStatus.CREATED).body("You have been signed up");

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
		Users user = userRepository.findByEmail(username);
		if (user != null) {
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
					user.getRoles());

		} else {
			logger.info("User Does not exist");
			throw new UsernameNotFoundException("Unable to fetch user details");
		}
	}

	public String forgotPasswordTokenGenarate(String email) {
		Users users = userRepository.findByEmail(email);
		if (users != null) {
			String token = UUID.randomUUID().toString();
			forgotPasswordToken.setToken(token);
			forgotPasswordToken.setUsers(users);
			forgotPasswordToken.setTokenCreatedTime(new Date(System.currentTimeMillis() + 3600000).getTime());
			forgotPasswordTokenRepository.save(forgotPasswordToken);
			return token;
		}
		return "User does not exist";
	}

	public ResponseEntity<String> forgotPassword(ForgotPassword forgotPassword) {
		Users users = userRepository.findByEmail(forgotPassword.getEmail());
		if (users != null) {
			String token = this.forgotPasswordTokenGenarate(forgotPassword.getEmail());
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(username);
			mailMessage.setTo(forgotPassword.getEmail());
			mailMessage.setSubject("Reset password link");
			mailMessage.setText("Click on the below link to reset the password \n"
					+ "http://localhost:3000/reset-password?token=" + token);
			javaMailSender.send(mailMessage);
			date = new Date(System.currentTimeMillis());
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body("Email has been sent with reset link. reset your password within 1 hour");
		} else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("User Doesnot exist.. Please enter the valid email.");
	}

	public ResponseEntity<String> resetPassword(String token, ResetPassword resetPassword) {
		ForgotPasswordToken passwordToken = forgotPasswordTokenRepository.findByToken(token);
		if (passwordToken != null
				&& passwordToken.getTokenCreatedTime() > (new Date(System.currentTimeMillis())).getTime()) {
			Users users = passwordToken.getUsers();

			if (users != null && resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword())
					&& passwordToken.getToken().equals(token)) {
				users.setPassword(encoder.encode(resetPassword.getNewPassword()));
				userRepository.save(users);
				forgotPasswordTokenRepository.delete(passwordToken);
				return ResponseEntity.status(HttpStatus.OK).body("Password has been changed");
			} else {
				forgotPasswordTokenRepository.delete(passwordToken);
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("New Password should not be same as old password");
			}

		}
		forgotPasswordTokenRepository.delete(passwordToken);
		return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
				.body("The link might be expired please regenerate the link");
	}

	public Users findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
