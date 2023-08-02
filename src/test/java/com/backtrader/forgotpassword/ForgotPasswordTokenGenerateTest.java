package com.backtrader.forgotpassword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;

import com.backtrader.repository.ForgotPasswordTokenRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.service.UserService;
import com.backtrader.userentity.ForgotPasswordToken;
import com.backtrader.userentity.Users;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
class ForgotPasswordTokenGenerateTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ForgotPasswordTokenRepository forgotPasswordTokenRepository;

	@InjectMocks
	private UserService userService;
	@InjectMocks
	private ForgotPasswordToken passwordToken;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		passwordToken=new ForgotPasswordToken();
		userService=new UserService(userRepository,forgotPasswordTokenRepository);
	}

	@Test
	void testForgotPasswordTokenGeneration_Success() {
		String email = "hugaranilkumar37@gmail.com";
		Users user = new Users(1, "anil", "hugar", email, 123456778890L, "anil1234", null);
		when(userRepository.findByEmail(email)).thenReturn(user);
		System.out.println(user.getFirstname());
		String generatedToken = userService.forgotPasswordTokenGenarate(email);
		assertThat(generatedToken).isNotNull();

		assertEquals("200 OK", HttpStatus.OK.toString());

	}

	@Test
	void testForgotPasswordTokenGeneration_UserNotFound() {
		String email = "hugaranilkumar37@gmail.com";
		when(userRepository.findByEmail(email)).thenReturn(null);
		String generatedToken = userService.forgotPasswordTokenGenarate(email);
		assertEquals("User does not exist", generatedToken);
		assertEquals("400 BAD_REQUEST", HttpStatus.BAD_REQUEST.toString());

	}
}
