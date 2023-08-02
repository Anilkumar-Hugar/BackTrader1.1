package com.backtrader.userentity;

import com.backtrader.userentity.Users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockDataWithSymbol {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	int id;
	private String symbol;
	private String openPrice;
	private String lowPrice;
	private String highPrice;
	private String closedPrice;
	private String date;
	private String information;
	@ManyToOne(cascade = CascadeType.ALL,targetEntity = Users.class)
	private Users users;
	
}
