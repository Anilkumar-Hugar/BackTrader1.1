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

	// CALLING THE ALPHAVANTAGE API AND GETTING THE STOCK DATA FROM IT
	public String getStockPrice(String symbol, String function) {

		// GETTING API KEY WHICH WE HAVE GENERATED TO USE ALPHAVANTAGE API
		String apiKey = configuration.getKey();

		// USING FINANCE API TO GET THE STOCK DATA
		String apiUrl = "https://www.alphavantage.co/query?symbol=" + symbol + "&function=" + function + "&apikey="
				+ apiKey + "&interval=" + 5;
		ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
		// RETURN THE STOCK VALUES AS A STRING
		return response.getBody();
	}

}
