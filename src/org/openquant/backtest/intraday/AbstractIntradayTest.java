package org.openquant.backtest.intraday;

import org.openquant.backtest.Candle;
import org.openquant.backtest.Position;
import org.openquant.backtest.QuantityCalculator;
import org.openquant.backtest.Series;


public abstract class AbstractIntradayTest {
	
	private IntradayCandleSeries candleSeries;
	
	private IntradayOrderManager orderManager = new IntradayOrderManager();
	
	public void reset() {
		
		orderManager.setSymbol(null);
		orderManager.reset();
	}

	public void setSymbol(String symbol) {
		orderManager.setSymbol(symbol);
	}

	public void processTick(Candle tick) {
		orderManager.processTick(tick);
	}

	public void processClose(Candle tick) {
		orderManager.processClose(tick);
	}

	public void buyAtLimit(double limitPrice, int quantity, OrderCallback callback) {
		orderManager.buyAtLimit(limitPrice, quantity, callback);
	}
	
	public void buyAtLimit(double limitPrice, QuantityCalculator quantityCalc, OrderCallback callback) {
		orderManager.buyAtLimit(limitPrice, quantityCalc, callback);
	}

	public void sellAtLimit(double limitPrice, int quantity, Position position, OrderCallback callback) {
		orderManager.sellAtLimit(limitPrice, quantity, position, callback);
	}

	public void sellAtStop(double stopPrice, int quantity, Position position, OrderCallback callback) {
		orderManager.sellAtStop(stopPrice, quantity, position, callback);
	}
	
	public void timeBasedExitOnClose(int days, Position position, OrderCallback callback){
		orderManager.timeBasedExitOnClose(days, position, callback);
	}

	public void setCandleSeries(IntradayCandleSeries candleSeries) {
		this.candleSeries = candleSeries;
		orderManager.setSymbol(candleSeries.getSymbol());
	}

	public IntradayCandleSeries getCandleSeries() {
		return candleSeries;
	}

	public void setOrderManager(IntradayOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IntradayOrderManager getOrderManager() {
		return orderManager;
	}
	
	public abstract void run();
	
	public Series dayCloseSeries(){
		return candleSeries.getDayCloseSeries();
	}
	
	public Series dayOpenSeries(){
		return candleSeries.getDayOpenSeries();
	}
	
	public Series dayLowSeries(){
		return candleSeries.getDayLowSeries();
	}
	
	public Series dayHighSeries(){
		return candleSeries.getDayHighSeries();
	}
	
	public int barsCount() {
		return candleSeries.size();
	}
	
	public double getClose(int index){
		return candleSeries.getDayCloseSeries().getAt(index);
	}
	
	public boolean hasOpenPositions() {
		return orderManager.hasOpenPositions();
	}
	
	public int getOpenPositionCount(){
		return orderManager.getOpenPositionCount();
	}

}
