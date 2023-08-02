package com.backtrader.signuptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.backtrader.model.UserModel;
import com.backtrader.repository.UserRepository;
import com.backtrader.service.UserService;
import com.backtrader.userentity.Roles;
import com.backtrader.userentity.Users;

@ExtendWith(MockitoExtension.class)
class UserSignupTest {
	Logger logger=mock(Logger.class);
	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder encoder;

	@InjectMocks
	private UserService userService;

	@Test
	void createUserTest_Success() {
		Users users = new Users();
		users.setId(1);
		users.setEmail("anilkumar@gmail.com");
		users.setFirstname("Anil kumar");
		users.setLastname("hugar");
		users.setPhone(7660017756L);
		users.setPassword("anil1234");
		userRepository.save(users);
		when(userRepository.findByEmail("anilkumarhugar@gmail.com")).thenReturn(users);
		Users user = (Users) userService.findByEmail("anilkumarhugar@gmail.com");
		assertEquals(user.getEmail(), users.getEmail());
		user.getFirstname().equals("Anil kumar");
	}

	@Test
	void createUserTestwithNullValues() {
		Users users = new Users();
		UserModel userModel = new UserModel();
		userRepository.save(users);
		logger.info("User has been created with user name {}",users.getFirstname());
		ResponseEntity<String> user = userService.createUser(userModel);
		assertThat(user).isNotNull();
		assertEquals(null, users.getEmail());
	}

	@Test
	void testLoadByUserName() {
		Users users = new Users();
		users.setId(1);
		users.setEmail("anilkumar@gmail.com");
		users.setFirstname("Anil kumar");
		users.setLastname("hugar");
		users.setPhone(7660017756L);
		users.setPassword("anil1234");
		Roles roles = new Roles();
		roles.setRole("ADMIN");
		List<Roles> role = new ArrayList<>();
		role.add(roles);
		users.setRoles(role);
		userRepository.save(users);
		when(userRepository.findByEmail(users.getEmail())).thenReturn(users);
		UserDetails userByUsername = userService.loadUserByUsername(users.getEmail());
		assertThat(userByUsername).isNotNull();
		assertEquals(users.getEmail(), userByUsername.getUsername());
	}
	@Test
	void testLoadByUSerName_Exception() {
		Users users=new Users();
		users.setEmail("anil@gmail.com");
		assertThrows(UsernameNotFoundException.class, 
				()->{
					when(userService.loadUserByUsername(users.getEmail())).thenReturn(null);
				});
		
	}

}
