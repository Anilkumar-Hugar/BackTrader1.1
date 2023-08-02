package com.backtrader.servicetest;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.backtrader.service.FinanceAPICall;

class FinanceAPICallTest {
	@Mock
	private FinanceAPICall financeAPICall;
	@Mock
	RestTemplate restTemplate;

	@BeforeEach
	public void setUp() {
		financeAPICall = new FinanceAPICall();
		restTemplate = new RestTemplate();
	}

	@Test
	void testFinanceApiCall() {
		String symbol = "AAPL";
		String function = "TIME_SERIES_WEEKLY";
		String url = "https://www.alphavantage.co/query?symbol=" + symbol + "&function=" + function;
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
		assertThat(responseEntity.getBody()).isNotNull();
	}
	
	@Test
	void testgetPriceFromJson() throws JSONException {
		String symbol = "AAPL";
		String function = "TIME_SERIES_WEEKLY";
		String url = "https://www.alphavantage.co/query?symbol=" + symbol + "&function=" + function;
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
		String response=responseEntity.getBody();
		JSONObject jsonObject = new JSONObject(response);
		assertThat(jsonObject).isNotNull();
	}
}
