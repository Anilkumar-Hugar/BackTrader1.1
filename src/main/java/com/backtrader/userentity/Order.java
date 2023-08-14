package com.backtrader.userentity;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
	public Order(LocalTime currentTime, String type2, String symbol2, int quantity2, double avgPrice2, String string,
			User users) {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String time;

	private String type;

	private String symbol;

	private int quantity;
	private double avgPrice;
	private String status;
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
	private User user;

}
