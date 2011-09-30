package org.openquant.backtest.intraday;

import org.openquant.backtest.Position;


public class SellIntent {
	
	private double price;

	private int quantity;
	
	private Position position;

	private OrderTimeFrame timeframe = OrderTimeFrame.GOOD_TILL_CANCELLED;
	
	private OrderCallback callback;

	public SellIntent(double price, int quantity, Position position, OrderTimeFrame timeframe, OrderCallback callback) {
		super();
		this.price = price;
		this.quantity = quantity;
		this.position = position;
		this.timeframe = timeframe;
		this.callback = callback;
	}

	public SellIntent(double price, int quantity, Position position, OrderCallback callback) {
		super();
		this.price = price;
		this.quantity = quantity;
		this.position = position;
		this.callback = callback;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public OrderTimeFrame getTimeframe() {
		return timeframe;
	}

	public void setTimeframe(OrderTimeFrame timeframe) {
		this.timeframe = timeframe;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public OrderCallback getCallback() {
		return callback;
	}
}