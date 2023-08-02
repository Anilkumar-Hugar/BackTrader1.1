package com.backtrader.passwordtest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.backtrader.repository.ForgotPasswordTokenRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.requestresponse.ResetPassword;
import com.backtrader.service.UserService;
import com.backtrader.userentity.ForgotPasswordToken;
import com.backtrader.userentity.Users;

class PasswordServiceTest {

    @Mock
    private ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserService passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testResetPassword_Successful() {
    	Users users=new Users(1, "anil", "kumar", "anil@gmail.com", 1234567899L, "anil1234", null);
        String token = "valid_token";
        Date futureTime = new Date(System.currentTimeMillis() + 3600000); // One hour in the future
        ForgotPasswordToken passwordToken = new ForgotPasswordToken();
        passwordToken.setToken(token);
        passwordToken.setTokenCreatedTime(futureTime.getTime());
        String newPassword = "new_password";
        String encodedPassword = "new_password";
        users.setPassword(encodedPassword);
        ResetPassword resetPassword = new ResetPassword(newPassword, encodedPassword);

        when(forgotPasswordTokenRepository.findByToken((token))).thenReturn(passwordToken);
        when(userRepository.save(any())).thenReturn(users);
        when(encoder.encode((newPassword))).thenReturn(encodedPassword);

        ResponseEntity<String> response = passwordService.resetPassword(token, resetPassword);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("New Password should not be same as old password", response.getBody());
    }

    @Test
    void testResetPassword_Conflict_SamePasswords() {
        String token = "valid_token";
        Date futureTime = new Date(System.currentTimeMillis() + 3600000); // One hour in the future
        ForgotPasswordToken passwordToken = new ForgotPasswordToken();
        passwordToken.setToken(token);
        passwordToken.setTokenCreatedTime(futureTime.getTime());

        Users users = new Users();
        String newPassword = "same_password";
        users.setPassword("encoded_same_password");

        ResetPassword resetPassword = new ResetPassword(newPassword, newPassword);

        when(forgotPasswordTokenRepository.findByToken(eq(token))).thenReturn(passwordToken);

        ResponseEntity<String> response = passwordService.resetPassword(token, resetPassword);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("New Password should not be same as old password", response.getBody());
    }

    @Test
    void testResetPassword_ExpiredToken() {
        String token = "expired_token";
        Date pastTime = new Date(System.currentTimeMillis() - 3600000); // One hour in the past
        ForgotPasswordToken passwordToken = new ForgotPasswordToken();
        passwordToken.setToken(token);
        passwordToken.setTokenCreatedTime(pastTime.getTime());

        when(forgotPasswordTokenRepository.findByToken(eq(token))).thenReturn(passwordToken);

        ResponseEntity<String> response = passwordService.resetPassword(token, new ResetPassword());

        assertEquals(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, response.getStatusCode());
        assertEquals("The link might be expired please regenerate the link", response.getBody());
    }

    @Test
    void testResetPassword_InvalidToken() {
        String token = "invalid_token";

        when(forgotPasswordTokenRepository.findByToken(eq(token))).thenReturn(null);

        ResponseEntity<String> response = passwordService.resetPassword(token, new ResetPassword());

        assertEquals(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, response.getStatusCode());
        assertEquals("The link might be expired please regenerate the link", response.getBody());
    }
}

