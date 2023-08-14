package com.backtrader.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backtrader.service.BuySellStockService;
import com.backtrader.userentity.Margin;
import com.backtrader.userentity.Order;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "Authorization")
public class BuySellStockController {
	@Autowired
	private BuySellStockService buySellStockService;

	// CODE FOR STOCK PURCHASE
	@PostMapping("/buy")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<String> buyStock(@RequestHeader("Authorization") String token, String symbol, double price,
			int quantity) {
		return buySellStockService.buyStock(token, symbol, price, quantity);
	}

	// THIS API WILL RETURN THE ORDERS PAGE FOR A PARTICULAR USER
	@GetMapping("/getorders")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<List<Order>> getAllOrders(@RequestHeader("Authorization") String token) {
		List<Order> orders = buySellStockService.orders(token);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	// API FOR SELLING THE STOCK
	@PostMapping("/sellstocks")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<String> sellStock(@RequestHeader("Authorization") String token, @RequestParam String symbol,
			@RequestParam int quantity) {
		return buySellStockService.sellStock(token, symbol, quantity);
	}

	// API FOR THE DASHBOARD TO CHECK THE MARGIN
	@PostMapping("/dashboard")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<Margin> showDashboard(@RequestHeader("Authorization") String token) {
		Margin margin = buySellStockService.calculateDashboard(token);
		return ResponseEntity.status(HttpStatus.OK).body(margin);
	}

}
