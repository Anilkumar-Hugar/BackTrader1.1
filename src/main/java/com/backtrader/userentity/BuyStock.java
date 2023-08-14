package com.backtrader.userentity;

import java.util.Date;

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
@Table(name = "Buy_stock")
public class BuyStock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private Date date;

	private String type;

	private String symbol;

	private int quantity;
	private double price;
	private double avgPrice;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
	private User user;

}
