package com.backtrader.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.repository.StockDataWithSymbolRepository;
import com.backtrader.repository.StockRespository;
import com.backtrader.repository.UserRepository;
import com.backtrader.userentity.Stock;
import com.backtrader.userentity.StockData;
import com.backtrader.userentity.StockDataWithSymbol;
import com.backtrader.userentity.StockPrice;
import com.backtrader.userentity.User;
import com.google.gson.Gson;

@Service
public class FinanceService {
	Logger logger = LoggerFactory.getLogger(FinanceService.class);
	@Autowired
	private StockRespository stockRespository;
	@Autowired
	private StockDataWithSymbolRepository stockDataWithSymbolRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FinanceAPICall financeAPICall;
	@Autowired
	private JwtToken jwtToken;

	// FINDING THE STOCK VALAUES FROM THE ALPHA VANTAGE API BASED ON THE GIVEN
	// TIMEFRAME
	public List<StockDataWithSymbol> getStockPrice(String token, String symbol, String function) {

		// STORING ALL THE TIME_SERIES IN THE ARRAYLIST AND USING LIST OPERATION TO
		// COMPARE THE TIME_SERIES VALUES
		List<String> information = new ArrayList<>(List.of("Intraday (5) open, high, low, close prices and volume",
				"Daily Time Series with Splits and Dividend Events",
				"Weekly Prices (open, high, low, close) and Volumes",
				"Monthly Prices (open, high, low, close) and Volumes"));

		// EXTRACTING THE TOKEN FROM UI AND REMOVING THE BEARER PREFIX FOR JWT TOKEN
		String extractedToken = token.substring(7);
		String username = jwtToken.getUserNameFromToken(extractedToken);

		// FINDING THE USER DETAILS FROM THE DATABASE
		User user = userRepository.findByEmail(username);
		List<StockDataWithSymbol> stockDataWithSymbolList = Collections.emptyList();

		if (user != null) {

			// IF USER AVAILABLE THEN CALL THE STOCK DATA METHOD AND SET THE VALUES TO STORE
			// THEM INTO DATABASE
			String json = financeAPICall.getStockPrice(symbol, function);
			Gson gson = new Gson();
			StockPrice stockPrice = gson.fromJson(json, StockPrice.class);
			Stock stock = new Stock();
			stock.setDate(new Date());
			stock.setNode(json);
			stock.setSymbol(symbol);
			stock.setUser(user);

			// USING SWITCH CASE TO COMPATRE THE TIME SERIES TO FETCH THE STOCK DATA
			switch (function) {
			case "TIME_SERIES_MONTHLY":
				stockDataWithSymbolList = processDataForStock(stockPrice.getStockMonthly(), information.get(3), user,
						symbol);
				break;
			case "TIME_SERIES_WEEKLY":
				stockDataWithSymbolList = processDataForStock(stockPrice.getStockweekly(), information.get(2), user,
						symbol);
				break;
			case "TIME_SERIES_DAILY_ADJUSTED":
				stockDataWithSymbolList = processDataForStock(stockPrice.getStockdaily(), information.get(1), user,
						symbol);
				break;
			case "TIME_SERIES_INTRADAY":
				stockDataWithSymbolList = processDataForStock(stockPrice.getStockIntraDay(), information.get(0), user,
						symbol);
				break;
			default:
				break;
			}

			stockRespository.save(stock);
		}

		// RETURNING THE FETCHED STOCK DATA BASES ON THE GIVEN TIME SERIES
		return stockDataWithSymbolList;
	}

	/*
	 * HERE WE ARE PROCESSING THE DATA FROM THE STOCK DATA AND SETTING UP THE VALUES
	 * WHICH ARE REQUIRED FOR OUR OPERATIONS
	 */
	private List<StockDataWithSymbol> processDataForStock(Map<String, StockData> stockDataMap, String information,
			User users, String symbol) {
		/*
		 * USING DECIMAL FORMAT METHOD TO SET THE EXTRACTED PRICE VALUES SO THAT THEY
		 * CAN HAVE ONLY 2 VALYES AFTER DECIMAL
		 */
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		List<StockDataWithSymbol> stockDataWithSymbolList = stockDataWithSymbolRepository
				.findByInformationAndSymbol(information, symbol);
		List<StockDataWithSymbol> stockdatalist = new ArrayList<>();
		int size = 0;

		for (Map.Entry<String, StockData> entry : stockDataMap.entrySet()) {

			// SETTING UP ALL THE STOCK DATA VALUES INTO THE OBJECT
			String date = entry.getKey();
			StockData stockData = entry.getValue();
			StockDataWithSymbol dataWithSymbol = new StockDataWithSymbol();
			dataWithSymbol.setSymbol(symbol);
			dataWithSymbol.setDate(date);
			dataWithSymbol.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
			dataWithSymbol.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
			dataWithSymbol.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
			dataWithSymbol.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
			dataWithSymbol.setInformation(information);
			dataWithSymbol.setUser(users);

			if (++size >= 31) {
				break;
			}

			if (stockDataWithSymbolList.isEmpty()) {
				stockdatalist.add(dataWithSymbol);
			} else {
				dataWithSymbol.setId(stockDataWithSymbolList.get(size - 1).getId());
				stockDataWithSymbolRepository.save(dataWithSymbol);
			}
		}

		if (stockDataWithSymbolList.isEmpty()) {

			// BATCH SAVING THE DATA AS A LIST WHICH WERE FETCHED FROM THE STOCK API
			return stockDataWithSymbolRepository.saveAll(stockdatalist);
		} else {

			// RETURNING THE STOCK DATA VALUES FROM THE DATABASE BASED ON THE SYMBOL AND
			// INFORMATION
			return stockDataWithSymbolRepository.findByInformationAndSymbol(information, symbol);
		}
	}

}
