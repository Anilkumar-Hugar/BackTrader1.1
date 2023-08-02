package com.backtrader.entitytest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.backtrader.userentity.Stock;
import com.backtrader.userentity.Users;

class StockTest {
	@MockBean
	private Stock stock;
	@Mock
	Users users;

	@BeforeEach
	public void setUp() {
		stock = new Stock();
		users = new Users();
	}

	@Test
	void testStockClass() {
		stock.setId(1);
		stock.setNode("{}");
		stock.setSymbol("AAPL");
		stock.setDate(new Date());
		stock.setUserid(users);
		assertThat(stock).isNotNull();
		assertEquals("Stock(id=1, node={}, date=" + new Date()
				+ ", userid=Users(id=0, firstname=null, lastname=null, email=null, phone=0, password=null, roles=null), symbol=AAPL)",
				stock.toString());
	}
	@Test
	void testStockConstructor() {
		stock=new Stock(1, "{}", new Date(), users, "AAPL");
		assertThat(stock).isNotNull();
	}
	

	@Test
	void testStockClass_Failure() {
		assertEquals(0, stock.getId());
		assertEquals(null, stock.getUserid());
		assertEquals(null, stock.getSymbol());
		assertEquals(null, stock.getNode());
		assertEquals(null, stock.getDate());
	}
}
