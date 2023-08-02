package com.backtrader.scheduler;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.repository.OrdersRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.userentity.Order;
import com.backtrader.userentity.Users;

@Service
public class SchedulerForFinanceData {
	Logger logger = LoggerFactory.getLogger(SchedulerForFinanceData.class);

	@Autowired
	private OrdersRepository ordersRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtToken jwtToken;

	public List<Order> findStock(String token) {
		token = token.substring(7);
		Users users = userRepository.findByEmail(jwtToken.getUserNameFromToken(token));
		if (users != null) {
			List<Order> stocklist = ordersRepository.findAll();
			logger.info("find Stock data got called with data {}",stocklist);
			return stocklist;
		}
		return Collections.emptyList();
	}
}
