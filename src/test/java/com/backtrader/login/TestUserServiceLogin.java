package com.backtrader.login;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.backtrader.repository.UserRepository;
import com.backtrader.service.UserService;
import com.backtrader.userentity.Users;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class TestUserServiceLogin {

	@Autowired
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	void testLoginUser_returns_User() {
		Users user = new Users(1, "anil", "hugar", "anilkumarhugar@gmail.com", 123456778890L, "anil1234", null);
		Users users = userRepository.save(user);
		assertThat(users).isNotNull();
		assertEquals("anil", users.getFirstname());
	}
	@Test
	void testLoginUser_returns_null() {
		Users user=new Users(1,null,null,null,0,null,null);
		Users users=userRepository.save(user);
		assertEquals(null, users.getEmail());
	}
}
