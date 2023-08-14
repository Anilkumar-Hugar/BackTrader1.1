package com.backtrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backtrader.userentity.BuyStock;
import com.backtrader.userentity.User;

@Repository
public interface BuyStockRepository extends JpaRepository<BuyStock, Integer> {

	List<BuyStock> findByUser(User user);

	BuyStock findBySymbolAndUser(String symbol, User user);

}
