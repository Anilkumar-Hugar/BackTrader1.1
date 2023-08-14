package com.backtrader.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backtrader.jwtconfiguration.FinanceConfiguration;
import com.backtrader.repository.CsvdatalistRepository;
import com.backtrader.userentity.CSVDataList;

@Service
public class StockSymbolsList {
	private static Logger logger = LoggerFactory.getLogger(StockSymbolsList.class);
	@Autowired
	private FinanceConfiguration config;
	@Autowired
	private CsvdatalistRepository csvdatalistRepository;

	// FINDING THE STOCK SYMBOLS FROM THE API AND STORING THEM INTO OUR DATABASE
	public void getlist() {

		// CHECKING WHETHER THERE ARE ANY SYMBOLS IN THE DATABASE
		List<CSVDataList> allSymbols = csvdatalistRepository.findAll();

		/*
		 * IF DATABASE IS EMPTY THEN CALLING THE ALPHA VANTAGE API AND STORING ALL THE
		 * STOCK SYMBOLS AND ITS NAME INTO DATABASE
		 */
		if (allSymbols.isEmpty()) {
			String apiKey = config.getKey();
			String csvUrl = "https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=" + apiKey;
			List<String[]> csvData = fetchCsvData(csvUrl);
			for (int i = 0; i < csvData.size(); i++) {
				String[] a = csvData.get(i);
				if (!a[0].equals("symbol")) {
					CSVDataList csvDataEntity = new CSVDataList();
					csvDataEntity.setSymbol(a[0]);
					csvDataEntity.setName(a[1]);
					csvDataEntity.setSaved(true);
					csvdatalistRepository.save(csvDataEntity);
				}
			}
		}
	}

	/*
	 * WE WILL BE GETTING THE CSV FILE FROM THE API WITH DATA AND BELWO WE ARE
	 * EXTRACTING THAT CSV FILE AND SETTING UP THE VALUES
	 */
	public static List<String[]> fetchCsvData(String csvUrl) {
		List<String[]> csvData = new ArrayList<>();
		try {
			URL url = new URL(csvUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] columns = line.split(",");
					csvData.add(columns);
				}
				reader.close();
			} else {
				logger.info("HTTP GET request failed with response code: {}", responseCode);
			}

		} catch (IOException e) {
			logger.error("Exception occured while fetching data {}", e.getMessage());
			e.printStackTrace();

		}
		return csvData;
	}

	/*
	 * HERE WE ARE RETRIEVING ALL THE STOCK DETAILS FROM THE DATABASE BY IGNORING
	 * THE CASE SENSITIVE
	 */
	public CSVDataList getSymbols(String symbol) {
		return csvdatalistRepository.findBySymbolIgnoreCase(symbol);
	}
}
