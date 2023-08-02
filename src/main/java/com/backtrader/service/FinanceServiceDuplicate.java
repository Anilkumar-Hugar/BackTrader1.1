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
import com.backtrader.userentity.MetaData;
import com.backtrader.userentity.Stock;
import com.backtrader.userentity.StockData;
import com.backtrader.userentity.StockDataWithSymbol;
import com.backtrader.userentity.StockPrice;
import com.backtrader.userentity.Users;
import com.google.gson.Gson;

@Service
public class FinanceServiceDuplicate {
	Logger logger = LoggerFactory.getLogger(FinanceServiceDuplicate.class);
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

	public List<StockDataWithSymbol> getStockPrice(String token, String symbol, String function) {
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		List<String> information = new ArrayList<>(List.of("Intraday (5) open, high, low, close prices and volume",
				"Daily Time Series with Splits and Dividend Events",
				"Weekly Prices (open, high, low, close) and Volumes",
				"Monthly Prices (open, high, low, close) and Volumes"));
		token = token.substring(7);
		String username = jwtToken.getUserNameFromToken(token);
		String json = financeAPICall.getStockPrice(symbol, function);
		Gson gson = new Gson();
		StockPrice stockPrice = gson.fromJson(json, StockPrice.class);
		Stock stock = gson.fromJson(json, Stock.class);
		stock.setDate(new Date());
		stock.setNode(json);
		stock.setSymbol(symbol);
		MetaData metaData = stockPrice.getMetaData();
		StockData stockData = null;
		Users users = userRepository.findByEmail(username);
		if (users != null) {
			stock.setUserid(users);
			if (function.equals("TIME_SERIES_MONTHLY")) {

				stockRespository.save(stock);
				List<StockDataWithSymbol> stockDataWithSymbolList = stockDataWithSymbolRepository
						.findByInformationAndSymbol(information.get(3), symbol);
				List<StockDataWithSymbol> stockdatalist = new ArrayList<>();
				if (stockDataWithSymbolList.isEmpty()) {
					int size = 0;

					for (Map.Entry<String, StockData> entry : stockPrice.getStockMonthly().entrySet()) {

						String date = entry.getKey();
						stockData = entry.getValue();
						StockDataWithSymbol dataWithSymbol = new StockDataWithSymbol();
						dataWithSymbol.setSymbol(metaData.getSymbol());
						dataWithSymbol.setDate(date);
						dataWithSymbol.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						dataWithSymbol.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						dataWithSymbol.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						dataWithSymbol.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						dataWithSymbol.setInformation(metaData.getInformation());
						dataWithSymbol.setUsers(users);
						size++;
						if (size >= 31)
							break;
						stockdatalist.add(dataWithSymbol);
					}
					return stockDataWithSymbolRepository.saveAll(stockdatalist);

				} else {
					int size = 0;
					StockDataWithSymbol stockValues = new StockDataWithSymbol();
					for (Map.Entry<String, StockData> entry : stockPrice.getStockMonthly().entrySet()) {
						++size;
						if (size >= 31)
							break;
						String date = entry.getKey();
						stockData = entry.getValue();
						stockValues.setSymbol(symbol);
						stockValues.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						stockValues.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						stockValues.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						stockValues.setDate(date);
						stockValues.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						stockValues.setId(stockDataWithSymbolList.get(size - 1).getId());
						stockValues.setInformation(metaData.getInformation());
						stockValues.setUsers(users);
						stockDataWithSymbolRepository.save(stockValues);
					}
					logger.info("Stored stock data {}", stockDataWithSymbolList);
					return stockDataWithSymbolRepository.findByInformationAndSymbol(information.get(3), symbol);
				}

			} else if (function.equals("TIME_SERIES_WEEKLY")) {
				stockRespository.save(stock);
				List<StockDataWithSymbol> stockDataWithSymbolList = stockDataWithSymbolRepository
						.findByInformationAndSymbol(information.get(2), symbol);
				if (stockDataWithSymbolList.isEmpty()) {
					int size = 0;
					List<StockDataWithSymbol> stockdatalist = new ArrayList<>();
					for (Map.Entry<String, StockData> entry : stockPrice.getStockweekly().entrySet()) {

						String date = entry.getKey();
						stockData = entry.getValue();
						StockDataWithSymbol dataWithSymbol = new StockDataWithSymbol();
						dataWithSymbol.setSymbol(metaData.getSymbol());
						dataWithSymbol.setDate(date);
						dataWithSymbol.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						dataWithSymbol.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						dataWithSymbol.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						dataWithSymbol.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						dataWithSymbol.setInformation(metaData.getInformation());
						dataWithSymbol.setUsers(users);
						size++;
						if (size >= 31)
							break;
						stockdatalist.add(dataWithSymbol);
					}
					return stockDataWithSymbolRepository.saveAll(stockdatalist);

				} else {
					int size = 0;
					StockDataWithSymbol stockValues = new StockDataWithSymbol();
					for (Map.Entry<String, StockData> entry : stockPrice.getStockweekly().entrySet()) {
						++size;
						if (size >= 31)
							break;
						String date = entry.getKey();
						stockData = entry.getValue();

						stockValues.setSymbol(symbol);
						stockValues.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						stockValues.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						stockValues.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						stockValues.setDate(date);
						stockValues.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						stockValues.setId(stockDataWithSymbolList.get(size - 1).getId());
						stockValues.setInformation(metaData.getInformation());
						stockValues.setUsers(users);
						stockDataWithSymbolRepository.save(stockValues);
					}
					logger.info("Stored stock data {}",
							stockDataWithSymbolRepository.findByInformationAndSymbol(information.get(2), symbol));
					return stockDataWithSymbolRepository.findByInformationAndSymbol(information.get(2), symbol);
				}
			} else if (function.equals("TIME_SERIES_DAILY_ADJUSTED")) {
				stockRespository.save(stock);
				List<StockDataWithSymbol> stockDataWithSymbolList = stockDataWithSymbolRepository
						.findByInformationAndSymbol(information.get(1), symbol);
				if (stockDataWithSymbolList.isEmpty()) {
					int size = 0;
					List<StockDataWithSymbol> stockdatalist = new ArrayList<>();
					for (Map.Entry<String, StockData> entry : stockPrice.getStockdaily().entrySet()) {

						String date = entry.getKey();
						stockData = entry.getValue();
						StockDataWithSymbol dataWithSymbol = new StockDataWithSymbol();
						dataWithSymbol.setSymbol(metaData.getSymbol());
						dataWithSymbol.setDate(date);
						dataWithSymbol.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						dataWithSymbol.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						dataWithSymbol.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						dataWithSymbol.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						dataWithSymbol.setInformation(metaData.getInformation());
						dataWithSymbol.setUsers(users);
						size++;
						if (size >= 31)
							break;
						stockdatalist.add(dataWithSymbol);
					}
					return stockDataWithSymbolRepository.saveAll(stockdatalist);

				} else {
					int size = 0;
					StockDataWithSymbol stockValues = new StockDataWithSymbol();
					for (Map.Entry<String, StockData> entry : stockPrice.getStockdaily().entrySet()) {
						++size;
						if (size >= 31)
							break;
						String date = entry.getKey();
						stockData = entry.getValue();

						stockValues.setSymbol(symbol);
						stockValues.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						stockValues.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						stockValues.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						stockValues.setDate(date);
						stockValues.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						stockValues.setId(stockDataWithSymbolList.get(size - 1).getId());
						stockValues.setInformation(metaData.getInformation());
						stockValues.setUsers(users);
						stockDataWithSymbolRepository.save(stockValues);
					}
					return stockDataWithSymbolRepository.findByInformationAndSymbol(information.get(1), symbol);
				}
			} else if (function.equals("TIME_SERIES_INTRADAY")) {
				stockRespository.save(stock);
				List<StockDataWithSymbol> stockDataWithSymbolList = stockDataWithSymbolRepository
						.findByInformationAndSymbol(information.get(0), symbol);
				if (stockDataWithSymbolList.isEmpty()) {
					int size = 0;
					List<StockDataWithSymbol> stockdatalist = new ArrayList<>();
					for (Map.Entry<String, StockData> entry : stockPrice.getStockIntraDay().entrySet()) {

						String date = entry.getKey();
						stockData = entry.getValue();
						StockDataWithSymbol dataWithSymbol = new StockDataWithSymbol();
						dataWithSymbol.setSymbol(metaData.getSymbol());
						dataWithSymbol.setDate(date);
						dataWithSymbol.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						dataWithSymbol.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						dataWithSymbol.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						dataWithSymbol.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						dataWithSymbol.setInformation(metaData.getInformation());
						dataWithSymbol.setUsers(users);
						size++;
						if (size >= 31)
							break;
						stockdatalist.add(dataWithSymbol);
					}
					return stockDataWithSymbolRepository.saveAll(stockdatalist);

				} else {
					int size = 0;
					StockDataWithSymbol stockValues = new StockDataWithSymbol();
					for (Map.Entry<String, StockData> entry : stockPrice.getStockIntraDay().entrySet()) {
						++size;
						if (size >= 31)
							break;
						String date = entry.getKey();
						stockData = entry.getValue();

						stockValues.setSymbol(symbol);
						stockValues.setHighPrice(decimalFormat.format(Double.valueOf(stockData.getHigh())));
						stockValues.setLowPrice(decimalFormat.format(Double.valueOf(stockData.getLow())));
						stockValues.setClosedPrice(decimalFormat.format(Double.valueOf(stockData.getClose())));
						stockValues.setDate(date);
						stockValues.setOpenPrice(decimalFormat.format(Double.valueOf(stockData.getOpen())));
						stockValues.setId(stockDataWithSymbolList.get(size - 1).getId());
						stockValues.setInformation(metaData.getInformation());
						stockValues.setUsers(users);

						stockDataWithSymbolRepository.save(stockValues);
					}
					return stockDataWithSymbolRepository.findByInformationAndSymbol(information.get(0), symbol);
				}
			}

		}

		return Collections.emptyList();
	}

}
