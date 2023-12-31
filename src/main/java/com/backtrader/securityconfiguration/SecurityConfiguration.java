package com.backtrader.securityconfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import com.backtrader.jwtconfiguration.JwtTokenFilter;
import com.backtrader.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
	@Autowired
	private JwtTokenFilter filter;
	@Autowired
	private UserService service;

	/*
	 * CREATING THE BEAN FOR THE SECURITY USING SECURITY FILTER CHAIN AND GIVING
	 * ACCESS TO THE USER INTERACTION ENDPOINTS AND AUTHENTICATING ALL OTHER ENDPOINTS USING
	 * METHOD LEVEL SECURITY
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {

		security.csrf(csrf -> csrf.disable())
		.cors(cors->cors.configurationSource(this.corsConfigurationSource()))
				.authorizeHttpRequests(request -> request
						.requestMatchers("/login", "/signup", "/forgot-passsword", "/reset-password", "/v3/api-docs/**",
								"/swagger-ui/**","/getsymbol","/getList","/getsymbol1")
								//, "/adminsignup", "/get", "/update","/buy","/getstocksystem")
						.permitAll().anyRequest().authenticated())
				.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		return security.build();
	}

	/* USING BCRYPTPASSWORD ENCODER TO ENCODE THE PASSWORD FOR SECURITY REASON */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * USING AUTRHENTICATION MANAGER TO AUTHENTICATE THE USER USING THE HTTPSECURITY
	 */
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
		AuthenticationManagerBuilder managerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
		managerBuilder.userDetailsService(service).passwordEncoder(passwordEncoder());
		return managerBuilder.build();
	}
	
	/*
	 * CREATING BEAN FOR THE CORS CONFIGURATION TO ALLOW THE ENDPOINTS TO USE
	 * BACKEND APPLICATION
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.addAllowedOrigin("*"); 
	    configuration.addAllowedHeader("*");
	    configuration.addAllowedMethod("*");
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
}
