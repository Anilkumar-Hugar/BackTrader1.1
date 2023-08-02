package com.backtrader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellStockRepository extends JpaRepository<com.backtrader.userentity.SellStock,Integer>{
	
}
