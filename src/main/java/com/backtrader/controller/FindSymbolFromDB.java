package com.backtrader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backtrader.service.StockSymbolsList;
import com.backtrader.userentity.CSVDataList;

@RestController
public class FindSymbolFromDB {
	@Autowired
	private StockSymbolsList stockSymbolsList;

	@GetMapping("/getsymbol")
	public ResponseEntity<CSVDataList> getSymbols(@RequestParam String symbol) {
		CSVDataList dataList = stockSymbolsList.getSymbols(symbol);
		if (dataList != null) {
			// RETURNING THE SYMBOLS FROM THE DATABASE
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(dataList);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}

	@GetMapping("/getsymbol1")
	public ResponseEntity<CSVDataList> getSymbol(@RequestParam String symbol1) {
		CSVDataList dataList = stockSymbolsList.getSymbols(symbol1);
		if (dataList != null) {
			// RETURNING THE SYMBOLS FROM THE DATABASE
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(dataList);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
	}
}
