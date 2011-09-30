package org.openquant.backtest.intraday;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Candle;
import org.openquant.backtest.Position;
import org.openquant.backtest.QuantityCalculator;

public class IntradayOrderManager {
	
	private Log log = LogFactory.getLog(IntradayOrderManager.class);
	
	private String symbol;

	private Set<Position> openPositions = new LinkedHashSet<Position>();

	private Set<Position> closedPositions = new LinkedHashSet<Position>();
	
	private List<BuyIntent> buyLimitIntents = new ArrayList<BuyIntent>();
	
	private List<SellIntent> sellLimitIntents = new ArrayList<SellIntent>();
	
	private List<SellIntent> sellStopIntents = new ArrayList<SellIntent>();
	
	private List<TimeBasedSellIntent> timeBaseCloseIntents = new ArrayList<TimeBasedSellIntent>();
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public Set<Position> getOpenPositions() {
		return openPositions;
	}

	public Collection<Position> getClosedPositions() {
		return closedPositions;
	}

	public void reset(){
		clearAllBuyIntents();
		clearAllSellIntents();
		closedPositions.clear();
		openPositions.clear();
	}
	
	private void clearAllBuyIntents(){
		buyLimitIntents.clear();
	}

	private void clearAllSellIntents(){
		sellLimitIntents.clear();
		sellStopIntents.clear();
		timeBaseCloseIntents.clear();
	}
	
	public void processTick(final Candle tick){
		//log.info("Tick " + tick);
		
		// limit buy orders		
		for (Iterator<BuyIntent> iter = buyLimitIntents.iterator(); iter.hasNext();) {
			BuyIntent intent = iter.next();
			if (tick.getClosePrice() <= intent.getPrice()){
				// create our order
				Position pos = new Position();
				pos.setSymbol(getSymbol());
				pos.setEntryPrice(tick.getClosePrice());
				pos.setQuantity(intent.getQuantity());
				pos.setEntryDate(tick.getDate());
				if(intent.isQuantityCalculated()){
					pos.setQuantityCalculator(intent.getQuantityCalc());
				}

				openPosition(pos);				
				
				intent.getCallback().success(pos);
				iter.remove();
				
				// since this is intended to be used by a single dataset at a time,
				// assume that multiple intraday orders of the same stock is not allowed
				// and clear out all other buy intents
				clearAllBuyIntents();
				
				break;
			}
		}
		
		// limit sell orders
		for (Iterator<SellIntent> iter = sellLimitIntents.iterator(); iter.hasNext();) {
			SellIntent intent = iter.next();
			
			if (tick.getClosePrice() >= intent.getPrice() ){

				Position pos = intent.getPosition();
				pos.setExitPrice(tick.getClosePrice());
				pos.setQuantity(intent.getQuantity());
				pos.setExitDate(tick.getDate());
				
				pos.setComments("SELL LIMIT sell on " + tick.getDate());
				
				closePosition(pos);				
				intent.getCallback().success(pos);
				iter.remove();
				
				clearAllSellIntents();
				break;
			}
			
		}
		
		for (Iterator<SellIntent> iter = sellStopIntents.iterator(); iter.hasNext();) {
			SellIntent intent = iter.next();
			
			if (tick.getClosePrice() <= intent.getPrice() ){
				
				Position pos = intent.getPosition();
				pos.setExitPrice(tick.getClosePrice());
				pos.setQuantity(intent.getQuantity());
				pos.setExitDate(tick.getDate());
				
				closePosition(pos);
				intent.getCallback().success(pos);
				iter.remove();
				
				clearAllSellIntents();
				break;
			}
		}
		
	}
	
	public void processClose(final Candle tick){
		
		//log.info("CLOSE Tick " + tick);
		
		// limit buy orders		
		for (Iterator<BuyIntent> iter = buyLimitIntents.iterator(); iter.hasNext();) {
			BuyIntent intent = iter.next();
			
			if (intent.getTimeframe().equals(OrderTimeFrame.END_OF_DAY)){
				intent.getCallback().expired();
				iter.remove();
			}
		}
		// limit sell orders		
		for (Iterator<SellIntent> iter = sellLimitIntents.iterator(); iter.hasNext();) {
			SellIntent intent = iter.next();
			
			if (intent.getTimeframe().equals(OrderTimeFrame.END_OF_DAY)){
				iter.remove();
			}
		}
		
		// limit sell orders		
		for (Iterator<SellIntent> iter = sellStopIntents.iterator(); iter.hasNext();) {
			SellIntent intent = iter.next();
			
			if (intent.getTimeframe().equals(OrderTimeFrame.END_OF_DAY)){
				iter.remove();
			}
		}
		
		for (Iterator<TimeBasedSellIntent> iter = timeBaseCloseIntents.iterator(); iter.hasNext();) {
			TimeBasedSellIntent intent = iter.next();
			
			if (intent.decrement() == 0){
				Position pos = intent.getPosition();
				pos.setExitPrice(tick.getClosePrice());
				pos.setExitDate(tick.getDate());
				
				pos.setComments("Time base sell on " + tick.getDate());
				
				closePosition(pos);
				intent.getCallback().success(pos);
				iter.remove();
				
				clearAllSellIntents();
			}
			
		}
		
	}
	
	private void openPosition(Position pos) {
		
		//log.info("Opening position " + pos);
		
		pos.setSymbol(symbol);
		openPositions.add(pos);
	}
	
	private void closePosition(Position pos) {
		
		//log.info("Closing position " + pos);
		
		if (openPositions.remove(pos)) {
			closedPositions.add(pos);
		} else {
			log.error("Could not find open position" + pos);
		}
	}
	
	public boolean hasOpenPositions() {
		return !openPositions.isEmpty();
	}
	
	public int getOpenPositionCount(){
		return openPositions.size();
	}
		
	/**
	 * An order to a broker to buy a specified quantity of a security at or
	 * below a specified price (called the limit price).
	 * 
	 * Read more:
	 * http://www.investorwords.com/648/buy_limit_order.html#ixzz17LLcHmCc
	 * 
	 */
	public void buyAtLimit(double limitPrice, int quantity, OrderCallback callback) {
		buyLimitIntents.add(new BuyIntent(limitPrice, quantity, callback));
	}
	
	
	public void buyAtLimit(double limitPrice, QuantityCalculator calculator, OrderCallback callback) {
		buyLimitIntents.add(new BuyIntent(limitPrice, calculator, callback));
	}
	
	/**
	 * An order to a broker to sell a specified quantity of a security at or
	 * above a specified price (called the limit price)
	 * 
	 * Read more:
	 * http://www.investorwords.com/4479/sell_limit_order.html#ixzz17LYIFrkY
	 * 
	 */
	public void sellAtLimit(double limitPrice, int quantity, Position position, OrderCallback callback){
		sellLimitIntents.add(new SellIntent(limitPrice, quantity, position, callback));
	}
	
	public void timeBasedSellAtClose(int days, Position position, OrderCallback callback){
		//sellLimitIntents.add(new SellIntent(limitPrice, quantity, position, callback));
		timeBaseCloseIntents.add(new TimeBasedSellIntent(days, position, callback));
	}
	
	/**
	 * A stop order for which the specified price is below the current market
	 * price and the order is to sell.
	 * 
	 * Read more: http://www.investorwords.com/4757/stop_loss.html#ixzz17LYl0YFK
	 * 
	 */
	public void sellAtStop(double stopPrice, int quantity, Position position, OrderCallback callback){
		sellStopIntents.add(new SellIntent(stopPrice, quantity, position, callback));
	}
	
	public void timeBasedExitOnClose(int days, Position position, OrderCallback callback){
		timeBaseCloseIntents.add( new TimeBasedSellIntent(days, position, callback) );
	}

}
