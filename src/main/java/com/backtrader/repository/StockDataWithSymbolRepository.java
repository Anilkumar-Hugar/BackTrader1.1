package com.backtrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtrader.userentity.StockDataWithSymbol;

public interface StockDataWithSymbolRepository extends JpaRepository<StockDataWithSymbol, Integer> {

	public List<StockDataWithSymbol> findByInformationAndSymbol(String information, String symbol);

	public List<StockDataWithSymbol> findBySymbol(String symbol);
}
