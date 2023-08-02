package com.backtrader.resetpasswordtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.backtrader.repository.ForgotPasswordTokenRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.requestresponse.ResetPassword;
import com.backtrader.service.UserService;
import com.backtrader.userentity.ForgotPasswordToken;
import com.backtrader.userentity.Users;

public class ResetPasswordTest {
	@BeforeEach
	public void setUp() {
		
	}
	@Test
	void testResetPassword_Success() {
		// Mock the repositories and encoder
		ForgotPasswordTokenRepository forgotPasswordTokenRepository = mock(ForgotPasswordTokenRepository.class);
		UserRepository userRepository = mock(UserRepository.class);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// Create test data
		String token = "testToken";
		Users user = new Users();
		user.setPassword(encoder.encode("oldPassword"));

		// Create the test controller
		UserService userController = new UserService();
		userController.setForgotPasswordTokenRepository(forgotPasswordTokenRepository);
		userController.setUserRepository(userRepository);
		userController.setEncoder(encoder);

		// Create the resetPassword request

		ResetPassword resetPassword = new ResetPassword();
		resetPassword.setNewPassword("newPassword");
		resetPassword.setConfirmPassword("newPassword");
		ForgotPasswordToken passwordToken = new ForgotPasswordToken();
		passwordToken.setToken(token);
		passwordToken.setTokenCreatedTime(System.currentTimeMillis() + 1000 * 60 * 60); // Valid for 1 hour
		passwordToken.setUsers(user);
		when(forgotPasswordTokenRepository.findByToken(token)).thenReturn(passwordToken);
		if (user != null && (resetPassword.getNewPassword()
				.equals(resetPassword.getConfirmPassword()) && passwordToken.getToken().equals(token))) {

			when(userRepository.save(any(Users.class))).thenReturn(user);

			// Call the resetPassword method
			ResponseEntity<String> response = userController.resetPassword(token, resetPassword);

			// Assert the response
			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals("Password has been changed", response.getBody());
		}
	}
}
