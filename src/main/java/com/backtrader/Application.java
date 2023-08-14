package com.backtrader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.backtrader.service.StockSymbolsList;

@SpringBootApplication
@EnableScheduling
public class Application implements ApplicationRunner {
	@Autowired
	private StockSymbolsList stockSymbolsList;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		/*
		 * CALLING THE ALPHAVANTAGE API METHOD TO STORE THE SYMBOLS INTO THE DATABASE
		 * FOR THE FIRST TIME WHEN THE APPLICATION RUN
		 */
		stockSymbolsList.getlist();

	}

}
