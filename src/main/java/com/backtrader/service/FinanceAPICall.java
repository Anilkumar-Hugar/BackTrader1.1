package com.backtrader.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backtrader.jwtconfiguration.FinanceConfiguration;

@Service
public class FinanceAPICall {
	@Autowired
	private FinanceConfiguration configuration;
	@Autowired
	private RestTemplate restTemplate;

	public String getStockPrice(String symbol, String function) {
		String apiKey = configuration.getKey();

		String apiUrl = "https://www.alphavantage.co/query?symbol=" + symbol + "&function=" + function + "&apikey="
				+ apiKey + "&interval=" + 5;
		ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
		return response.getBody();
	}

}
