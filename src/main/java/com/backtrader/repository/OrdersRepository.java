package com.backtrader.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backtrader.userentity.Order;
import com.backtrader.userentity.Users;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Integer> {
	List<Order> findAllByUser(Users user);

}
