package com.backtrader.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtrader.userentity.CSVDataList;

public interface CsvdatalistRepository extends JpaRepository<CSVDataList, Integer>{
	CSVDataList findBySymbolIgnoreCase(String symbol);
}
