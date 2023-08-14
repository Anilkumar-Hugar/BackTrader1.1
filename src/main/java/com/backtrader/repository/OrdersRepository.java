package com.backtrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backtrader.userentity.Order;
import com.backtrader.userentity.User;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Integer> {
	List<Order> findAllByUser(User user);
	List<Order>findBySymbol(String symbol);

}
