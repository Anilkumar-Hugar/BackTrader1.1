package com.backtrader.signuptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.model.UserModel;
import com.backtrader.repository.ForgotPasswordTokenRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.service.UserService;
import com.backtrader.userentity.Users;

class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    private JavaMailSender javaMailSender;
    private JwtToken jwtToken;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        forgotPasswordTokenRepository = mock(ForgotPasswordTokenRepository.class);
        javaMailSender = mock(JavaMailSender.class);
        jwtToken = mock(JwtToken.class);

        userService = new UserService();
        userService.setUserRepository(userRepository);
        userService.setForgotPasswordTokenRepository(forgotPasswordTokenRepository);
        userService.setJavaMailSender(javaMailSender);
        userService.setJwtToken(jwtToken);
    }

    @Test
    void testCreateUser() {
        UserModel userModel = new UserModel("John", "Doe", "john.doe@example.com", 1234567890L, "password");
        if(userModel!=null) {
        when(userRepository.findByEmail(userModel.getEmail())).thenReturn(null);

        ResponseEntity<String> response = userService.createUser(userModel);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("You have been signed up", response.getBody());
        }
    }
    @Test
    void testCreateUser_WithNullUserModel() {
        UserModel userModel = null;
        ResponseEntity<String> response = userService.createUser(userModel);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User Details does not looks correct", response.getBody());
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        UserModel userModel = new UserModel("John", "Doe", "john.doe@example.com", 1234567890L, "password");
        Users existingUser = new Users();
        when(userRepository.findByEmail(userModel.getEmail())).thenReturn(existingUser);

        ResponseEntity<String> response = userService.createUser(userModel);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User Already Exist", response.getBody());
    }

    @Test
    void testForgotPasswordTokenGenarate_UserExists() {
        String email = "john.doe@example.com";
        Users users = new Users();
        when(userRepository.findByEmail(email)).thenReturn(users);

        String token = userService.forgotPasswordTokenGenarate(email);
        assertThat(token).isNotNull();
        
    }

    @Test
    void testForgotPasswordTokenGenarate_UserDoesNotExist() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        String token = userService.forgotPasswordTokenGenarate(email);

        assertEquals("User does not exist", token);
    }

}
