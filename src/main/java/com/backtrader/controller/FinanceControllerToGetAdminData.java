package com.backtrader.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.backtrader.scheduler.FinanceDataForAdmin;
import com.backtrader.userentity.Order;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "Authorization")
public class FinanceControllerToGetAdminData {
	Logger logger = LoggerFactory.getLogger(FinanceControllerToGetAdminData.class);
	@Autowired
	private FinanceDataForAdmin financeData;

	@GetMapping("/getadmindata")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<List<Order>> findAllStockData(@RequestHeader("Authorization") String token) {

		// API TO FETCH THE STOCK ORDER DATA FOR ALL THE USERS
		List<Order> stockData = financeData.findStock(token);
		return ResponseEntity.ok(stockData);
	}

}