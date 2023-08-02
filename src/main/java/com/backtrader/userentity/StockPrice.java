package com.backtrader.userentity;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPrice {
	@SerializedName("Meta Data")
	private MetaData metaData;
	@SerializedName("Time Series (Daily)")
	private Map<String, StockData> stockdaily;
	@SerializedName("Time Series (5)")
	private Map<String, StockData> stockIntraDay;
	@SerializedName("Weekly Time Series")
	private Map<String, StockData>stockweekly;
	@SerializedName("Monthly Time Series")
	private Map<String, StockData>stockMonthly;
}
