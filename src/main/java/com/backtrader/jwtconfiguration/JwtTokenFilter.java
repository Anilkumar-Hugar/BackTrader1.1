package com.backtrader.jwtconfiguration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backtrader.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtToken jwtToken;
	@Autowired
	private UserService service;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = this.splitToken(request);

		// VALIDATING THE JWT TOKEN
		if (token != null && jwtToken.validateJwtToken(token)) {

			// FINDING THE USERNAME FROM THE JWT TOKEN USING GETUSERNAMEFROMTOKEN METHOD
			String username = jwtToken.getUserNameFromToken(token);
			UserDetails details = service.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details,
					null, details.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}

		/*
		 * APPLYING THE FILTER TO THE REQUEST AND RESPONSE SO THAT IT WILL VALIDATE THE
		 * TOKEN
		 */
		filterChain.doFilter(request, response);

	}

	public String splitToken(HttpServletRequest request) {
		String token = null;
		// GETTING THE TOKEN FROM THE HTTP REQUEST AND REMOVING THE BEARER PREFIX
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7, header.length());
		}
		return token;
	}

}
