package com.backtrader.userentity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MetaData {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	@SerializedName("1. Information")
	private String information;

	@SerializedName("2. Symbol")
	private String symbol;

	@SerializedName("3. Last Refreshed")
	private String lastRefreshed;

	@SerializedName("4. Output Size")
	private String outputSize;

	@SerializedName("5. Time Zone")
	private String timeZone;
//	@ManyToMany(targetEntity = StockData.class,cascade = CascadeType.ALL)
//	private List<StockData> stockData=new ArrayList<>();
}
