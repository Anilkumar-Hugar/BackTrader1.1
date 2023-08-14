package com.backtrader.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.backtrader.jwtconfiguration.JwtToken;
import com.backtrader.repository.BuyStockRepository;
import com.backtrader.repository.MarginRepository;
import com.backtrader.repository.OrdersRepository;
import com.backtrader.repository.SellStockRepository;
import com.backtrader.repository.UserRepository;
import com.backtrader.userentity.BuyStock;
import com.backtrader.userentity.Margin;
import com.backtrader.userentity.Order;
import com.backtrader.userentity.SellStock;
import com.backtrader.userentity.User;

@Service
public class BuySellStockService {
	Logger logger = LoggerFactory.getLogger(BuySellStockService.class);
	private double marginUsed = 0;

	@Value("${api.value}")
	String apikey;
	@Autowired
	private MarginRepository marginrepository;
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

	private final RestTemplate restTemplate = new RestTemplate();

	public String symbolData(String symbol) throws RestClientException {
		// GET THE UPDATED DATA FROM THE FINANCE API
		String apiUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE" + "&symbol=" + symbol + "&apikey="
				+ apikey;
		logger.info("updated data has been fetched for {}", symbol);
		ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
		return response.getBody();

	}

	public String getPricefromJSON(String jsonData) {

		// FETCHING THE PRICE FROM THE STOKC DATA
		JSONObject jsonObject = new JSONObject(jsonData);
		String price = jsonObject.getJSONObject("Global Quote").getString("05. price");
		logger.info("updated price of the stock is {}", price);

		// RETURN THE PRICE FROM THE STOCK
		return decimalFormat.format(Double.parseDouble(price));

	}

	public ResponseEntity<String> sellStock(String token, String symbol, int quantity) throws RestClientException {

		// EXTRACTING THE TOKEN FROM THE BEARER TOKEN WHICH WE GOT FROM THE UI
		String extractedToken = token.substring(7);
		String email = jwtToken.getUserNameFromToken(extractedToken);

		// FETCHING DATA FROM THE DATABASE USING EMAIL
		User userData = userRepository.findByEmail(email);
		logger.info("User {} is selling stocks of {} of quantity {}", userData.getFirstname(), symbol, quantity);
		String type = "SELL";

		/*
		 * FETCHING THE BOUGHT DATA FROM THE DATABASE AND CHECKS WHETHER THERE ARE ANY
		 * STOCKS WHICH BOUGHT RECENTLY
		 */
		BuyStock item = buyStockRepository.findBySymbolAndUser(symbol, userData);

		if (item != null && item.getQuantity() >= quantity) {
			String jsonData = symbolData(symbol);
			double price = Double.parseDouble(getPricefromJSON(jsonData));
			logger.info("Current price is {}", price);

			double totalprice = quantity * price;

			/*
			 * Calling the margin calculation method to calculate the margin based on the
			 * current price.
			 */
			calculateMargindata(userData, type, quantity * price);

			// CREATING THE SELL STOCK AND SETTING UP THE DATA TO THE SELL DATABASE
			SellStock sell = new SellStock();
			Date currentDate = new Date(System.currentTimeMillis());
			sell.setDate(currentDate);
			sell.setPrice(price);
			sell.setSymbol(symbol);
			sell.setUser(userData);
			sell.setType("SELL");
			sell.setQuantity(quantity);
			sell.setAvgPrice(totalprice);

			// SAVE THE SOLD STOCK DATA INTO DATABASE
			sellStockRepository.save(sell);
			logger.info("User {} sold stocks {} of quantity {}", userData.getFirstname(), symbol, quantity);

			// CREATING NEW ORDER OBJECT AND SAVING THE SOLD STOCK VALUES INTO DATABASE
			Order newOrder = new Order();
			newOrder.setTime(currentDate.toString());
			newOrder.setType(type);
			newOrder.setAvgPrice(sell.getAvgPrice());
			newOrder.setSymbol(symbol);
			newOrder.setUser(userData);
			newOrder.setStatus("OPEN");
			newOrder.setQuantity(quantity);

			// SAVING THE SOLD STOCKS TO THE DATABASE
			ordersRepository.save(newOrder);

			/*
			 * IF THE QUANTITIES ARE EQUAL TO THE QUANTITIES AVAILABLE IN BUY DATABASE THEN
			 * WE CAN DELETE ALL THE BOUGHT STOCK
			 */
			if (quantity == item.getQuantity()) {
				buyStockRepository.delete(item);
			} else {

				/*
				 * IF QUANTITIES ARE NOT EQUAL THE WE CAN JUST ADJUST THE QUANTITIES BY
				 * SUBTRACTING THEM FROM TOTAL QUANTITIES
				 */
				item.setQuantity(item.getQuantity() - quantity);
				item.setQuantity(quantity);

				// SAVE THE ADJUSTED DATA
				buyStockRepository.save(item);
			}

			// IF EVERYTHING GOES WELL RETURN THE RESPONSE CODE ACCEPTED WITH SUCCESS
			// MESSEGE
			return ResponseEntity.status(HttpStatus.ACCEPTED).body("Stock has been sold");
		} else {

			// IF ERROR OCCURS RETURN THE STATUS CODE OF BAD REQUEST WITH ERROR MESSEGE
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid sell operation.");
		}
	}

	// API TO BUY STOCK FROM THE FINANCE API
	public ResponseEntity<String> buyStock(String token, String symbol, double price, int quantity) {
		// Extract the email from the token by removing Bearer prefix
		String email = jwtToken.getUserNameFromToken(token.substring(7));
		User userData = userRepository.findByEmail(email);

		// Create a new order to store the bought data
		Order newOrder = new Order();
		newOrder.setQuantity(quantity);

		logger.info("User {} is buying {} stocks of quantity {}", userData.getFirstname(), symbol, quantity);

		/*
		 * getting the buy stock data from the database using the below method for
		 * logged in user
		 */
		BuyStock item = buyStockRepository.findBySymbolAndUser(symbol, userData);
		String type = "BUY";
		double totalPrice = quantity * price;

		/*
		 * checking margin values from the database for the user to check the current
		 * margin so that he should not exceed his margin value while purchase
		 */
		Margin margin = marginrepository.findByUser(userData);

		if (totalPrice <= margin.getMarginAvailable()) {
			if (item != null) {
				updateExistingItem(item, userData, type, totalPrice, quantity, price);
			} else {
				createNewItem(userData, type, totalPrice, quantity, price, symbol);
			}

			/*
			 * checking the available stock from the buy database and adding the purchased
			 * stock into orders table with stock details
			 */
			newOrder.setTime(new Date(System.currentTimeMillis()).toString());
			newOrder.setType("BUY");
			newOrder.setSymbol(symbol);
			newOrder.setUser(userData);
			newOrder.setStatus("OPEN");

			// storing thr bought data into orders table
			ordersRepository.save(newOrder);

			logger.info("User {} bought {} stocks of quantity {} at a unit cost of {}", userData.getFirstname(), symbol,
					quantity, price);

			// returning the success message with response code OK
			return ResponseEntity.status(HttpStatus.OK).body("BUYING_STOCK");
		} else {

			// returning the error message with status code forbidden
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error");
		}
	}

	private void updateExistingItem(BuyStock item, User userData, String type, double totalPrice, int quantity,
			double price) {
		int oldQuantity = item.getQuantity();
		double oldAvgPrice = item.getAvgPrice();

		/* Calculating margin values for the user for existing buy data */
		calculateMargindata(userData, type, totalPrice);

		/*
		 * updating the price for the existing stock data and setting up it into
		 * database
		 */
		double updatedPrice = oldAvgPrice + totalPrice;
		int updatedQuantity = oldQuantity + quantity;

		/* setting up the buystock data values and store it into buystock table */
		item.setQuantity(updatedQuantity);
		item.setPrice(price);
		item.setAvgPrice(Double.valueOf(decimalFormat.format(updatedPrice)));
		item.setDate(new Date(System.currentTimeMillis()));
		item.setType(type);
		item.setUser(userData);

		// storing all the updated values into database using jpa repository methods
		buyStockRepository.save(item);
	}

	private void createNewItem(User userData, String type, double totalPrice, int quantity, double price,
			String symbol) {

		// creating object for the buystock to set the purchased stock values
		BuyStock newitem = new BuyStock();
		calculateMargindata(userData, type, totalPrice);

		// setting values for the buystock table to save the new order
		newitem.setQuantity(quantity);
		newitem.setAvgPrice(Double.valueOf(decimalFormat.format(totalPrice)));
		newitem.setDate(new Date());
		newitem.setPrice(price);
		newitem.setSymbol(symbol);
		newitem.setType(type);
		newitem.setUser(userData);

		// storing the purchased new stock into database
		buyStockRepository.save(newitem);
	}

	// calculating the margin values based on particular user
	public void calculateMargindata(User userData, String type, Double updatedprice) {

		double marginAvailable = 0;
		logger.info("User {} is getting margin data for {} with price {}", userData.getFirstname(), type, updatedprice);
		Margin userMargin = marginrepository.findByUser(userData);

		if (type.equals("BUY")) {

			/*
			 * calculating the available margin for a user and for buy operation the margin
			 * value will be deducted from the available margin
			 */
			marginAvailable = userMargin.getMarginAvailable();
			if (marginAvailable >= 0) {

				// setting up the margin values and calculation accordingly
				marginUsed = userMargin.getMarginUsed();
				marginAvailable -= updatedprice;
				marginUsed += updatedprice;
			}
		} else {

			// If operation is sell just deducting the margin used and adding it into
			// available margin
			marginAvailable = userMargin.getMarginAvailable();
			marginUsed = userMargin.getMarginUsed();
			marginAvailable += updatedprice;
			marginUsed -= updatedprice;
		}

		userMargin.setMarginAvailable(Double.valueOf(decimalFormat.format(marginAvailable)));
		userMargin.setMarginUsed(Double.valueOf(decimalFormat.format(marginUsed)));

		// Once all the calculation done saving margin values into database
		marginrepository.save(userMargin);
	}

	// This method will give the margin details for the loggedin user
	public Margin calculateDashboard(String token) {

		String extractedToken = token.substring(7);
		String email = jwtToken.getUserNameFromToken(extractedToken);
		User userData = userRepository.findByEmail(email);

		// finding the margin values from the database and storing it in margin object
		Margin margin = marginrepository.findByUser(userData);

		if (userData != null && margin != null && margin.getUser().equals(userData)) {
			logger.info("Available margin value is {}", margin.getMarginAvailable());

			// returning the margin values
			return margin;
		} else {

			// if user does not exist or margin values are null then returning the default
			// margin values
			return new Margin(0, 10000000, 10000000, 0, new Date(System.currentTimeMillis()), userData);
		}

	}

	// Here getting the orders data from the database based on the bearer token
	public List<Order> orders(String token) {

		// removing the bearer token from the token and finding user to get the order
		// details
		String extractedToken = token.substring(7);
		String email = jwtToken.getUserNameFromToken(extractedToken);
		User userData = userRepository.findByEmail(email);

		// returning the order details found from the database
		return ordersRepository.findAllByUser(userData);
	}

	public String getSystemUpdatedStock(String token, String symbol) {
		// removing the bearer token from the token
		String extractedToken = token.substring(7);
		if (jwtToken.validateJwtToken(extractedToken)) {

			// returning the updated stock data to perform buy and sell operations
			return symbolData(symbol);
		} else
			return "Inavlid token";

	}

}