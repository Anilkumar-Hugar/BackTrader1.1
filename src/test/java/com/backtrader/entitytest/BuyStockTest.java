package com.backtrader.entitytest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.backtrader.userentity.BuyStock;
import com.backtrader.userentity.Users;

public class BuyStockTest {
	@Mock
	private BuyStock buyStock;

	@Mock
	private Users users;

	@BeforeEach
	public void setUp() {
		buyStock = new BuyStock();
		users = new Users();
	}

	@Test
	void testBuyStock_Success() {
		buyStock.setId(1);
		buyStock.setQuantity(1);
		buyStock.setSymbol("AAPL");
		buyStock.setType("OPEN");
		buyStock.setAvgPrice(123.98);
		buyStock.setDate(new Date());
		buyStock.setUser(users);
		buyStock.setPrice(123.98);
		assertThat(buyStock).isNotNull();
		assertEquals("AAPL", buyStock.getSymbol());
	}

	@Test
	void testBuyStock_Failure() {
		buyStock.getId();
		buyStock.getQuantity();
		buyStock.getSymbol();
		buyStock.getType();
		buyStock.getAvgPrice();
		buyStock.getDate();
		buyStock.getUser();
		buyStock.getPrice();
		assertEquals(null, buyStock.getSymbol());
		assertNotEquals("AAPL", buyStock.getSymbol());

	}

	@Test
	void testBuyStock_toString() {
		buyStock.setId(1);
		buyStock.setQuantity(1);
		buyStock.setSymbol("AAPL");
		buyStock.setType("OPEN");
		buyStock.setAvgPrice(123.98);
		buyStock.setDate(new Date());
		buyStock.setUser(users);
		buyStock.setPrice(123.98);
		assertEquals(
				"BuyStock(id=1, date="+new Date()+", type=OPEN, symbol=AAPL, quantity=1, price=123.98, avgPrice=123.98, user=Users(id=0, firstname=null, lastname=null, email=null, phone=0, password=null, roles=null))",
				buyStock.toString());
	}
}
