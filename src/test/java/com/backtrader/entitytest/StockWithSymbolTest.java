package com.backtrader.entitytest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.backtrader.userentity.StockDataWithSymbol;
import com.backtrader.userentity.Users;

class StockWithSymbolTest {
	@Mock
	StockDataWithSymbol stockDataWithSymbol;
	@Mock
	Users users;
	
	@BeforeEach
	void setUp() {
		stockDataWithSymbol=new StockDataWithSymbol();
		users=new Users();
	}
	@Test
	void testStockDataWithSymbol_Success() {
		stockDataWithSymbol.setId(1);
		stockDataWithSymbol.setSymbol("AAPL");
		stockDataWithSymbol.setDate(new Date(System.currentTimeMillis()).toString());
		stockDataWithSymbol.setHighPrice("123.99");
		stockDataWithSymbol.setClosedPrice("112.00");
		stockDataWithSymbol.setOpenPrice("123.00");
		stockDataWithSymbol.setLowPrice("112.00");
		stockDataWithSymbol.setUsers(users);
		stockDataWithSymbol.setInformation("Monthly Data");
		assertThat(stockDataWithSymbol).isNotNull();
		assertEquals("AAPL", stockDataWithSymbol.getSymbol());
	}
	@Test
	void testStockDataWithSymbol_Failure() {
		assertEquals(0,stockDataWithSymbol.getId());
		assertEquals(null, stockDataWithSymbol.getSymbol());;
		assertNotEquals(new Date(System.currentTimeMillis()).toString(), stockDataWithSymbol.getDate());;
		assertNotEquals("123", stockDataWithSymbol.getHighPrice());;
		assertNotEquals("", stockDataWithSymbol.getClosedPrice());
		assertNotEquals("132", stockDataWithSymbol.getOpenPrice());;
		assertNotEquals("112", stockDataWithSymbol.getLowPrice());;
		assertEquals(null, stockDataWithSymbol.getUsers());
		assertEquals(null, stockDataWithSymbol.getInformation());;
	}
}
