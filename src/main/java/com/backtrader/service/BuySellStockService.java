package com.backtrader.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.repository.BuyStockRepository;
import com.backtrader.repository.OrdersRepository;
import com.backtrader.repository.SellStockRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.userentity.BuyStock;
import com.backtrader.userentity.Order;
import com.backtrader.userentity.SellStock;
import com.backtrader.userentity.Users;

@Service
public class BuySellStockService {

	@Value("${api.value}")
	String apikey;

	@Autowired
	private SellStockRepository sellStockRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BuyStockRepository buyStockRepository;

	@Autowired
	private OrdersRepository ordersRepository;
	@Autowired
	private JwtToken jwtToken;
	final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

	Logger logger = org.slf4j.LoggerFactory.getLogger(BuySellStockService.class);
	private final RestTemplate restTemplate = new RestTemplate();

	public String symbolData(String symbol) throws RestClientException {

		String apiUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE" + "&symbol=" + symbol + "&apikey="
				+ apikey;
		logger.info("updated data has been fetched for {}", symbol);
		ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
		return response.getBody();

	}

	public String getPricefromJSON(String jsonData) {
		JSONObject jsonObject = new JSONObject(jsonData);
		return decimalFormat
				.format(Double.parseDouble(jsonObject.getJSONObject("Global Quote").getString("05. price")));

	}

	public String sellStock(String token, String symbol, int quantity) throws RestClientException {
		token = token.substring(7);
		String email = jwtToken.getUserNameFromToken(token);
		Users userData = userRepository.findByEmail(email);
		logger.info("User {} is selling stocks of {} of quantity {}", userData.getFirstname(), symbol, quantity);
		Order newOrder = new Order();
		newOrder.setQuantity(quantity);
		BuyStock item = buyStockRepository.findBySymbolAndUser(symbol, userData);
		if (item != null && quantity <= item.getQuantity()) {
			String jsonData = symbolData(symbol);
			String strPrice = getPricefromJSON(jsonData);
			double price = Double.parseDouble(strPrice);
			logger.info(" current price is {}", price);
			double totalprice = quantity * price;
			item.setQuantity(item.getQuantity() - quantity);
			SellStock buy = new SellStock();
			Date currentDate = new Date(System.currentTimeMillis());
			buy.setDate(currentDate);
			buy.setPrice(price);
			buy.setSymbol(symbol);
			buy.setUser(userData);
			buy.setType("SELL");
			buy.setQuantity(quantity);
			buy.setAvgPrice(totalprice);
			sellStockRepository.save(buy);
			logger.info("User {} sold stocks {}  of quantity {}", userData.getFirstname(), symbol, quantity);
			newOrder.setTime(currentDate.toString());
			newOrder.setType("SELL");
			newOrder.setAvgPrice(buy.getAvgPrice());
			newOrder.setSymbol(symbol);
			newOrder.setUser(userData);
			newOrder.setStatus("OPEN");
			ordersRepository.save(newOrder);
			return "SOLD_STOCK";
		}
		logger.info("User {} have trouble selling Stock", userData.getFirstname());
		return "USER_DON'T_HAVE_ENOUGH_QUANTITY_TO_SELL or NO_STOCK_BROUGHT_BY_THIS_USER";
	}

	public String buyStock(String token, String symbol, double price, int quantity) {
		token = token.substring(7);
		String email = jwtToken.getUserNameFromToken(token);
		Users userData = userRepository.findByEmail(email);
		Order newOrder = new Order();
		newOrder.setQuantity(quantity);
		logger.info("User {} is buying stocks of {} of quantity {}", userData.getFirstname(), symbol, quantity);
		BuyStock item = buyStockRepository.findBySymbolAndUser(symbol, userData);
		if (item != null) {
			int oldQuantity = item.getQuantity();
			double oldAvgPrice = item.getAvgPrice();
			double updatedprice = oldAvgPrice + (quantity * price);
			quantity = oldQuantity + quantity;
			item.setQuantity(quantity);
			item.setPrice(price);
			item.setAvgPrice(updatedprice);
			Date date = new Date(System.currentTimeMillis());
			item.setDate(date);
			item.setSymbol(symbol);
			item.setType("BUY");
			item.setUser(userData);
			newOrder.setAvgPrice(item.getAvgPrice());
			buyStockRepository.save(item);
		} else {
			BuyStock newitem = new BuyStock();
			double totalprice = (quantity * price);
			newitem.setQuantity(quantity);
			newitem.setAvgPrice(totalprice);
			Date date = new Date();
			newitem.setDate(date);
			newitem.setPrice(price);
			newitem.setSymbol(symbol);
			newitem.setType("BUY");
			newitem.setUser(userData);
			newOrder.setAvgPrice(newitem.getAvgPrice());
			buyStockRepository.save(newitem);

		}
		newOrder.setTime(new Date(System.currentTimeMillis()).toString());
		newOrder.setType("BUY");
		newOrder.setSymbol(symbol);
		newOrder.setUser(userData);
		newOrder.setStatus("OPEN");
		ordersRepository.save(newOrder);
		logger.info("User {} brought stocks of {} of quantity {} where each unit costs {}", userData.getFirstname(),
				symbol, quantity, price);
		return "BUYING_STOCK";
	}

	public List<Order> orders(String token) {
		token = token.substring(7);
		String email = jwtToken.getUserNameFromToken(token);
		Users userData = userRepository.findByEmail(email);
		return ordersRepository.findAllByUser(userData);
	}
	
	public String getSystemUpdatedStock(String token,String symbol) {
		token=token.substring(7);
		if(jwtToken.validateJwtToken(token)) {
			 return symbolData(symbol);
			 //convert values of prices into 2 decimal values
		}
		else
			return "Inavlid token";
		
	}

}