package com.backtrader.jwtconfiguration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.backtrader.service.UserService;
import com.backtrader.userentity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtToken {
	long expDate = 30000;
	@Value("${app.key}")
	private String key;
	@Autowired
	private UserService userService;

	public String generateJwtToken(String email) {

		// FINDING THE USER USING EMAIL WITH BUILT IN METHOD
		UserDetails details = userService.loadUserByUsername(email);
		User user = userService.findByEmail(email);

		// AFTER FINDING THE USER DETAILS SETTING THEM TO THE TOKEN
		return Jwts.builder().setSubject(details.getUsername()).setIssuedAt(new Date()).claim("phone", user.getPhone())
				.claim("firstname", user.getFirstname()).claim("lastname", user.getLastname())
				.claim("roles", user.getRole()).signWith(SignatureAlgorithm.HS256, key.getBytes()).compact();
	}

	public String getUserNameFromToken(String token) {
		// HERE WE CAN EXTRACT THE EMAIL FROM THE TOKEN
		return Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean isTokenExpired(String authToken) {
		return Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(authToken).getBody().getExpiration()
				.before(new Date(System.currentTimeMillis()));
	}

	public boolean validateJwtToken(String authToken) {
		// CHECKING IF THE TOKEN IS VALID OR NOT
		return (getUserNameFromToken(authToken) != null);
	}

}
