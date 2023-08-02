package com.backtrader.entitytest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backtrader.userentity.Roles;
import com.backtrader.userentity.Users;
@ExtendWith(MockitoExtension.class)
class UsersTest {

	@InjectMocks
	private Users users;
	private List<Roles> role = new ArrayList<>();;

	@Mock
	private Roles roles;

	@BeforeEach
	public void setup() {
		roles = new Roles();
		roles.setId(1);
		roles.setRole("USER");
		role.add(roles);
		users = Users.builder().id(1).firstname("John").lastname("Doe").email("john.doe@example.com").phone(1234567890L)
				.password("password").roles(role).build();
	}

	@Test
	void testConstructor() {
		Users users = new Users(1, "anil", "kumar", "anil@gmail.com", 1234567890L, "anil1234", role);
		assertThat(users).isNotNull();
	}

	@Test
	void testGettersAndSetters() {
		// Test getters
		assertEquals(1, users.getId());
		assertEquals("John", users.getFirstname());
		assertEquals("Doe", users.getLastname());
		assertEquals("john.doe@example.com", users.getEmail());
		assertEquals(1234567890L, users.getPhone());
		assertEquals("password", users.getPassword());
		assertNotNull(users.getRoles());

		// Test setters
		users.setId(2);
		users.setFirstname("Jane");
		users.setLastname("Smith");
		users.setEmail("jane.smith@example.com");
		users.setPhone(9876543210L);
		users.setPassword("newpassword");
		users.setRoles(role);

		assertEquals(2, users.getId());
		assertEquals("Jane", users.getFirstname());
		assertEquals("Smith", users.getLastname());
		assertEquals("jane.smith@example.com", users.getEmail());
		assertEquals(9876543210L, users.getPhone());
		assertEquals("newpassword", users.getPassword());
		assertNotNull(users.getRoles());
	}

	@Test
	void testAddRole() {
		users.setRoles(role);

		assertNotNull(users.getRoles());
		assertEquals(1, users.getRoles().size());
		assertEquals("USER", users.getRoles().get(0).getRole());
	}

	@Test
	void testFindRoles() {
		List<Roles> roleList = new ArrayList<>();
		roleList.add(roles);

		List<Roles> result = users.getRoles();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(roles, result.get(0));
	}

	@Test
	void testToString() {
		String user = users.toString();
		String returned = "" + users;
		assertEquals(user, returned);
	}
}
