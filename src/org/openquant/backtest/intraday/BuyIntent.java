package org.openquant.backtest.intraday;

import org.openquant.backtest.QuantityCalculator;


public class BuyIntent{
	
	private double price;
	
	private int quantity;
	
	private OrderTimeFrame timeframe = OrderTimeFrame.END_OF_DAY;		
	
	private OrderCallback callback;
	
	private QuantityCalculator quantityCalc;

	public BuyIntent(double price, int quantity, OrderCallback callback){
		this.price = price;
		this.quantity = quantity;
		this.callback = callback;
	}
	
	public BuyIntent(double price, QuantityCalculator quantityCalc, OrderCallback callback){
		this.price = price;
		this.quantityCalc = quantityCalc;
		this.callback = callback;
	}
			
	public BuyIntent(double price, int quantity, OrderTimeFrame timeframe, OrderCallback callback) {
		super();
		this.price = price;
		this.quantity = quantity;
		this.timeframe = timeframe;
		this.callback = callback;
	}

	public boolean isQuantityCalculated(){
		return quantityCalc != null;
	}
	public double getPrice() {
		return price;
	}

	public OrderCallback getCallback() {
		return callback;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public OrderTimeFrame getTimeframe() {
		return timeframe;
	}
	
	public void setTimeframe(OrderTimeFrame timeframe) {
		this.timeframe = timeframe;
	}

	public QuantityCalculator getQuantityCalc() {
		return quantityCalc;
	}
}
