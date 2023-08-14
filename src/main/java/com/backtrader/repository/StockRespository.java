package com.backtrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtrader.userentity.Stock;
import com.backtrader.userentity.User;

public interface StockRespository extends JpaRepository<Stock, Integer> {
	public List<Stock> findByUser(User users);
	public List<Stock> findBySymbol(String symbol);
}
