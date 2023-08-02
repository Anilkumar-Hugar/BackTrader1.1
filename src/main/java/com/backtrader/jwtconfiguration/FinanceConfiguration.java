package com.backtrader.jwtconfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FinanceConfiguration {
	@Value("$(api.value)")
	private String apiKey;

	public String getKey() {
		return apiKey;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
