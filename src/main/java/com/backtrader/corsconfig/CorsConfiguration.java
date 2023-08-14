package com.backtrader.corsconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

	@Bean
	public WebMvcConfigurer configurer() {
		return new WebMvcConfigurer() {

			/*
			 * SETTING UP THE CORS CONFIGURATION BY ALLOWING THE 3001 AND 3000 PORT SO THAT
			 * BACKEND APPLICATION CAN RUN THE THE SAME PORT
			 */
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOriginPatterns("http://localhost:3001", "http://localhost:3000")
						.allowedOrigins("http://localhost:3000").allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
						.allowedHeaders("*").exposedHeaders("Authorization").allowCredentials(true);

			}
		};
	}
}
