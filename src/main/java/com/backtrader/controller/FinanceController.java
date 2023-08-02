package com.backtrader.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backtrader.service.BuySellStockService;
import com.backtrader.service.FinanceServiceDuplicate;
import com.backtrader.userentity.StockDataWithSymbol;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "Authorization")
public class FinanceController {

	Logger logger = LoggerFactory.getLogger(FinanceController.class);
	@Autowired
	private FinanceServiceDuplicate financeServiceDuplicate;
	@Autowired
	private BuySellStockService buySellStockService;

	@GetMapping(value = "/getstock")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<List<StockDataWithSymbol>> get(@RequestHeader("Authorization") String token,
			@RequestParam String symbol, @RequestParam String function) {
		logger.info("Getting the stock data based on {} with symbol {}",function,symbol);
		var stockPriceList = financeServiceDuplicate.getStockPrice(token, symbol, function);
		return new ResponseEntity<>(stockPriceList, HttpStatus.OK);

	}

	@GetMapping("/getupdatedstock")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<?> getStockDataOnSystemPickedDate(@RequestHeader("Authorization") String token,
			String symbol) {
	return	ResponseEntity.ok(buySellStockService.getSystemUpdatedStock(token,symbol));
	}
}
