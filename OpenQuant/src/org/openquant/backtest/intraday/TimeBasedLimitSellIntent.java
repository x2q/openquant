package org.openquant.backtest.intraday;

import org.openquant.backtest.Position;

public class TimeBasedLimitSellIntent extends SellIntent {
	
	private int decrementor;
	
	private int dayBarCount;

	public TimeBasedLimitSellIntent(int dayBarCount, double price, int quantity, Position position, OrderTimeFrame timeframe,
			OrderCallback callback) {
		super(price, quantity, position, timeframe, callback);
		this.dayBarCount = dayBarCount;
		this.decrementor = dayBarCount;
	}

	public TimeBasedLimitSellIntent(int dayBarCount, double price, int quantity, Position position, OrderCallback callback) {
		super(price, quantity, position, callback);
		this.dayBarCount = dayBarCount;
		this.decrementor = dayBarCount;
	}
	
	public boolean isActive(){
		return decrementor == 0;
	}
	
	public int getDayBarCount() {
		return dayBarCount;
	}
	
	public int decrement(){
		return decrementor--;
	}

}
