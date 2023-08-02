package com.backtrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtrader.userentity.Stock;
import com.backtrader.userentity.Users;

public interface StockRespository extends JpaRepository<Stock, Integer> {
	public List<Stock> findByUserid(Users users);
	public List<Stock> findBySymbol(String symbol);
}
