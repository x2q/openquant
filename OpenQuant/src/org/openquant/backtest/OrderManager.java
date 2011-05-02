package org.openquant.backtest;

/*
Copyright (c) 2010, Jay Logelin
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following 
conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the JQuant nor the names of its 
contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrderManager {

	private Log log = LogFactory.getLog(OrderManager.class);

	private Set<Position> openPositions = new LinkedHashSet<Position>();

	private Set<Position> closedPositions = new LinkedHashSet<Position>();

	private String symbol;

	public OrderManager() {
	}

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
	
	public boolean containsOpenPosition(Position position){
		return openPositions.contains(position);
	}

	public boolean hasOpenPositions() {
		return !openPositions.isEmpty();
	}

	private void openPosition(Position pos) {
		pos.setSymbol(symbol);
		openPositions.add(pos);
	}

	private void closePosition(Position pos) {
		if (openPositions.remove(pos)) {
			closedPositions.add(pos);
		} else {
			log.error("Could not find open position" + pos);
		}
	}
	
	public Position buyAtMarket(int barIndex, double marketPrice, int quantity, String comments, CandleSeries data){
		Candle current = data.get(barIndex);
		
		log.debug("ENTER Position : " + current + " at market Price: " + marketPrice);

		Position pos = new Position();
		pos.setComments(comments);
		pos.setEntryPrice(marketPrice);
		pos.setQuantity(quantity);
		pos.setEntryDate(current.getDate());

		openPosition(pos);
		
		return pos;
		
	}

	/**
	 * An order to a broker to buy a specified quantity of a security at or
	 * below a specified price (called the limit price).
	 * 
	 * Read more:
	 * http://www.investorwords.com/648/buy_limit_order.html#ixzz17LLcHmCc
	 * 
	 * @param limitPrice
	 * @param barIndex
	 * @param data
	 * @return all of the possible Positions for the CandleSeries
	 */
	public Position buyAtLimit(int barIndex, double limitPrice, int quantity, String comments,CandleSeries data) {

		Position pos = null;
		
		Candle current = data.get(barIndex);

		if (current.getOpenPrice() <= limitPrice) {
			log.debug("ENTER Position : " + current + " at limit Price: " + current.getOpenPrice());

			pos = new Position();
			pos.setComments(comments);
			pos.setEntryPrice(current.getOpenPrice());
			pos.setQuantity(quantity);
			pos.setEntryDate(current.getDate());

			openPosition(pos);

		} else if (current.getLowPrice() <= limitPrice) {
			log.debug("ENTER Position : " + current + " at limit Price: " + limitPrice);
			// if it falls within the day's range
			pos = new Position();
			pos.setComments(comments);
			pos.setEntryPrice(limitPrice);
			pos.setEntryDate(current.getDate());
			pos.setQuantity(quantity);

			openPosition(pos);
		}
		
		return pos;

	}

	/**
	 * An order to a broker to sell a specified quantity of a security at or
	 * above a specified price (called the limit price)
	 * 
	 * Read more:
	 * http://www.investorwords.com/4479/sell_limit_order.html#ixzz17LYIFrkY
	 * 
	 * @param barIndex
	 * @param position
	 * @param limitPrice
	 * @param data
	 */
	public void sellAtLimit(int barIndex, Position position, double limitPrice, String comments, CandleSeries data) {
		if (openPositions.contains(position)) {

			Candle current = data.get(barIndex);

			if (current.getOpenPrice() >= limitPrice) {
				log.debug("EXIT Position(LIMITSALE) : " + current + " at limit Price: " + current.getOpenPrice());
				
				position.appendComment(comments);
				position.setExitPrice(current.getOpenPrice());
				position.setExitDate(current.getDate());
				closePosition(position);
				return;
			} else if (current.getHighPrice() >= limitPrice) {
				log.debug("EXIT Position(LIMITSALE) : " + current + " at limit Price: " + limitPrice);
				
				position.appendComment(comments);
				position.setExitPrice(limitPrice);
				position.setExitDate(current.getDate());
				closePosition(position);
				return;
			}

		} else {
			log.error("Could not find position" + position);
		}
	}
	
	public void sellAtLimitIntraday(int barIndex, Position position, double limitPrice, String comments, CandleSeries data) {
		if (openPositions.contains(position)) {

			Candle current = data.get(barIndex);

			if (current.getHighPrice() >= limitPrice) {
				log.debug("EXIT Position(LIMITSALE) : " + current + " at limit Price: " + limitPrice);
				
				position.appendComment(comments);
				position.setExitPrice(limitPrice);
				position.setExitDate(current.getDate());
				closePosition(position);
				return;
			}

		} else {
			log.error("Could not find position" + position);
		}
	}

	/**
	 * A stop order for which the specified price is below the current market
	 * price and the order is to sell.
	 * 
	 * Read more: http://www.investorwords.com/4757/stop_loss.html#ixzz17LYl0YFK
	 * 
	 * @param barIndex
	 * @param position
	 * @param stopPrice
	 * @param data
	 */
	public void sellAtStop(int barIndex, Position position, double stopPrice, String comments, CandleSeries data) {
		if (openPositions.contains(position)) {

			Candle current = data.get(barIndex);

			if (current.getOpenPrice() <= stopPrice) {
				log.debug("EXIT Position(STOPLOSS) : " + current + " at stop Price: " + current.getOpenPrice());
				
				position.appendComment(comments);
				position.setExitPrice(current.getOpenPrice());
				position.setExitDate(current.getDate());
				closePosition(position);
				return;
			} else if (current.getLowPrice() <= stopPrice) {
				log.debug("EXIT Position(STOPLOSS) : " + current + " at stop Price: " + stopPrice);
				
				position.appendComment(comments);
				position.setExitPrice(stopPrice);
				position.setExitDate(current.getDate());
				closePosition(position);
				return;
			}

		} else {
			log.error("Could not find position" + position);
		}
	}

	public Position getLastOpenPosition() {
		Position last = null;
		for (Position pos : openPositions) {
			last = pos;
		}
		return last;
	}
}
