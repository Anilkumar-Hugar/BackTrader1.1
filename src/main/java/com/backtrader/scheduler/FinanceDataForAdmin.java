package com.backtrader.scheduler;

import java.util.ArrayList;
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
import com.backtrader.userentity.User;

@Service
public class FinanceDataForAdmin {
	Logger logger = LoggerFactory.getLogger(FinanceDataForAdmin.class);
	@Autowired
	private OrdersRepository ordersRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtToken jwtToken;

	// GETTING ALL THE STOCK DATA FROM THE DATABASE AND PRITNG IT ON THE ADMIN PAGE
	public List<Order> getData(String token) {

		// EXTRACTING BEARER TOKEN AND REMOVING THE BEARER PREFIX
		String exctractedToken = token.substring(7);
		User users = userRepository.findByEmail(jwtToken.getUserNameFromToken(exctractedToken));

		if (users != null) {

			// RETURNING ALL THE STOCK ORDERS FROM THE DATABASE AND DISPLAYING IT ON THE
			// ADMIN PAGE
			List<Order> fetchedstocklist = ordersRepository.findAll();
			logger.info("The data has been fetched from the Orders with all the users {}", fetchedstocklist);
			return fetchedstocklist;
		} else {
			return Collections.emptyList();
		}
	}

	public List<Order> findStock(String token) {
		// EXTRACTING BEARER TOKEN AND REMOVING THE BEARER PREFIX
		String exctractedToken = token.substring(7);
		User users = userRepository.findByEmail(jwtToken.getUserNameFromToken(exctractedToken));
		
		if (users != null) {
			/*
			 * RETURNING ALL THE STOCK ORDERS FROM THE DATABASE AND DISPLAYING IT ON THE
			 * ADMIN PAGE
			 */
			List<Order> stocklist = ordersRepository.findAll();
			List<Order> adminDataList = new ArrayList<>();
			for (Order list : stocklist) {
				
				//setting up the order data to print on the admin page
				Order adminData = new Order();
				adminData.setSymbol(list.getSymbol());
				adminData.setType(list.getType());
				adminData.setStatus(list.getStatus());
				adminData.setQuantity(list.getQuantity());
				adminData.setTime(list.getTime());
				adminData.setAvgPrice(list.getAvgPrice());
				adminData.setUser(list.getUser());
				adminDataList.add(adminData);
			}
			logger.info("Stock data is being displayed from the DB on the Admin page");
			
			//returning all the stocks to print on the admin page
			return adminDataList;
		} else
			return Collections.emptyList();
	}
}
