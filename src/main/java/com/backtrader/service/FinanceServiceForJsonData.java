package com.backtrader.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.backtrader.jwtconfiguration.FinanceConfiguration;
import com.backtrader.repository.StockRespository;
import com.backtrader.repository.UserRepository;
import com.backtrader.userentity.Stock;
import com.backtrader.userentity.Users;
import com.google.gson.Gson;

@Service
public class FinanceServiceForJsonData {
	@Autowired
	private StockRespository stockRespository;
	@Autowired
	private UserRepository userRepository;
	private final FinanceConfiguration config;

	public FinanceServiceForJsonData(FinanceConfiguration config) {
		this.config = config;
	}

	public ResponseEntity<Object> getStockAsJson(Authentication authentication, String symbol, String function)
			throws IOException, InterruptedException {
		String email = authentication.getName();
		Users users = userRepository.findByEmail(email);
		if (users != null) {
			String apiKey = config.getKey();

			String apiUrl = "https://www.alphavantage.co/query?function=" + function + "&symbol=" + symbol + "&apikey="
					+ apiKey + "&interval=" + 5;

			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(apiUrl)).build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			String json = response.body();
			Gson gson = new Gson();
			Stock stock = gson.fromJson(json, Stock.class);

			Date date = new Date();
			stock.setDate(date);
			stock.setNode(json);
			stock.setUserid(users);

			return ResponseEntity.status(HttpStatus.FOUND).body(stockRespository.save(stock));
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock not found");
	}
}
