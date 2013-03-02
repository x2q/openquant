package org.openquant.backtest.intraday;

import org.openquant.backtest.Position;


public class TimeBasedSellIntent {
	
	private int decrementor;
	
	private int dayBarCount;
	
	private Position position;

	private OrderCallback callback;

	public TimeBasedSellIntent(int dayBarCount, Position position, OrderCallback callback) {
		super();
		this.dayBarCount = dayBarCount;
		this.decrementor = dayBarCount;
		this.position = position;
		this.callback = callback;
	}

	public int getDayBarCount() {
		return dayBarCount;
	}

	public Position getPosition() {
		return position;
	}

	public OrderCallback getCallback() {
		return callback;
	}
	
	public int decrement(){
		return decrementor--;
	}

	
}