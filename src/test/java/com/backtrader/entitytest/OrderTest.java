package com.backtrader.entitytest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.backtrader.userentity.Order;
import com.backtrader.userentity.Users;

class OrderTest {
	@Mock
	Order order;
	@Mock
	Users users;
	
	@BeforeEach
	public void setUp() {
		order=new Order();
		users=new Users();
	}
	
	@Test
	void testOrder_Success() {
		order.setQuantity(2);
		order.setAvgPrice(123.00);
		order.setId(1);
		order.setStatus("BUY");
		order.setSymbol("AAPL");
		order.setTime(new Date(System.currentTimeMillis()).toString());
		order.setType("OPEN");
		order.setUser(users);
		assertThat(order).isNotNull();
		assertEquals("BUY", order.getStatus());
		
	}
	@Test
	void testOrder_Failure() {
		assertNotEquals(1, order.getId());
		assertNotEquals("SELL", order.getStatus());
		assertNotEquals("CLOSE", order.getType());
		assertNotEquals(123, order.getAvgPrice());
		assertNotEquals(users, order.getUser());
		assertEquals(null, order.getTime());
		assertEquals(null, order.getSymbol());
		assertEquals(0, order.getQuantity());
	}
}
