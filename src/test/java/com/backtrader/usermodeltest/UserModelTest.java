package com.backtrader.usermodeltest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.backtrader.model.UserModel;

public class UserModelTest {

	private UserModel userModel;

	@BeforeEach
	public void setUp() {
		userModel = UserModel.builder().firstname("John").lastname("Doe").email("john.doe@example.com")
				.phone(1234567890L).password("secretpassword").build();
	}

	@Test
	public void testUserModelCreation() {
		assertNotNull(userModel);
	}

	@Test
	public void testGetters() {
		assertEquals("John", userModel.getFirstname());
		assertEquals("Doe", userModel.getLastname());
		assertEquals("john.doe@example.com", userModel.getEmail());
		assertEquals(1234567890L, userModel.getPhone());
		assertEquals("secretpassword", userModel.getPassword());
	}

	@Test
	public void testToString() {
		assertNotNull(userModel.toString());
	}
}
