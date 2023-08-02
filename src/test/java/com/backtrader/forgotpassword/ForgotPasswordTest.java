package com.backtrader.forgotpassword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.repository.ForgotPasswordTokenRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.requestresponse.ForgotPassword;
import com.backtrader.service.UserService;
import com.backtrader.userentity.ForgotPasswordToken;
import com.backtrader.userentity.Users;

import jakarta.mail.MessagingException;

class ForgotPasswordTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private JavaMailSender javaMailSender;
	@Mock
	private ForgotPasswordTokenRepository forgotPasswordTokenRepository;
	@InjectMocks
	private ForgotPasswordToken forgotPasswordToken;
	@InjectMocks
	private UserService userService;
	BCryptPasswordEncoder encoder;
	Date date;
	Logger logger = mock(Logger.class);
	JwtToken jwtToken;
	

	@BeforeEach
	public void setUp() {
		date = new Date();
		encoder = new BCryptPasswordEncoder();
		forgotPasswordToken = new ForgotPasswordToken();
		jwtToken = new JwtToken();
		userService = new UserService(userRepository, forgotPasswordTokenRepository);
		userService = new UserService(logger, forgotPasswordToken, "", date, encoder, javaMailSender,
				forgotPasswordTokenRepository, userRepository, jwtToken);
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testForgotPassword_UserExists() throws MessagingException { // Arrange
		String email = "anilkumarhugar@gmail.com";
		ForgotPassword forgotPassword = new ForgotPassword(email);
		Users user = new Users();
		user.setEmail(email);
		when(userRepository.findByEmail(email)).thenReturn(user);

		// Act
		ResponseEntity<String> response = userService.forgotPassword(forgotPassword);

		// Assert
		verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertEquals("Email has been sent with reset link. reset your password within 1 hour", response.getBody());
	}

	@Test
	void testForgotPassword_UserDoesNotExist() throws MessagingException {
		// Arrange
		String email = "nonexistent@example.com";
		ForgotPassword forgotPassword = new ForgotPassword(email);
		when(userRepository.findByEmail(email)).thenReturn(null);

		// Act
		ResponseEntity<String> response = userService.forgotPassword(forgotPassword);

		// Assert
		verify(javaMailSender, times(0)).send(any(SimpleMailMessage.class));
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("User Doesnot exist.. Please enter the valid email.", response.getBody());
	}

	@Test
	void testGetter() {
		assertThat(userService.getForgotPasswordTokenRepository()).isNotNull();
		assertThat(userService.getForgotPasswordToken()).isNotNull();
		assertThat(userService.getEncoder()).isNotNull();
		assertThat(userService.getJavaMailSender()).isNotNull();
		assertThat(userService.getJwtToken()).isNotNull();
		assertThat(userService.getLogger()).isNotNull();
		assertThat(userService.getDate()).isNotNull();
		assertThat(userService.getUserRepository()).isNotNull();
		assertThat(userService.getUsername()).isNotNull();
	}

	@Test
	void testSetter() {
		userService = new UserService();
		userService.setDate(date);
		userService.setEncoder(encoder);
		userService.setForgotPasswordToken(forgotPasswordToken);
		userService.setForgotPasswordTokenRepository(forgotPasswordTokenRepository);
		userService.setJavaMailSender(javaMailSender);
		userService.setJwtToken(jwtToken);
		userService.setLogger(logger);
		userService.setUserRepository(userRepository);
		userService.setUsername("anil");
		assertThat(userService).isNotNull();
	}
}
