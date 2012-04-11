package org.openquant.backtest.report;

/*
Copyright (c) 2011, Jay Logelin
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Order;
import org.openquant.backtest.Position;

public abstract class AbstractReport {

	private Log log = LogFactory.getLog(AbstractReport.class);

	private double totalCapitalAndEquity;

	private double slippage;

	private double commission;

	private double availableCash;

	private Collection<Position> closedPositions;

	private Collection<Position> openPositions;

	private Order extractEntryOrder(Position position) {

		return new Order(true, position.getEntryDate(),
				position.getEntryPrice() * position.getQuantity(), position);
	}

	private Order extractExitOrder(Position position) {

		return new Order(false, position.getExitDate(), position.getExitPrice()
				* position.getQuantity(), position);
	}

	public double getTotalCapitalAndEquity() {
		return totalCapitalAndEquity;
	}
	
	private int compareEntries(Order one, Order two){
		
		if ((one.isEntry() && two.isEntry()) || (!one.isEntry() && !two.isEntry()) ){
			return 0;
		}
		if (one.isEntry() && !two.isEntry()){
			return -1;
		}
		if ( !one.isEntry() && two.isEntry()){
			return 1;
		}
		return 0;
	}

	public void process() {
		List<Order> allOrders = new ArrayList<Order>();

		for (Position position : closedPositions) {

			Order exit = extractExitOrder(position);
			allOrders.add(exit);
			position.setExit(exit);

			Order entry = extractEntryOrder(position);
			allOrders.add(entry);
			position.setEntry(entry);

		}

		Collections.sort(allOrders, new Comparator<Order>() {

			/**
			 * Compare and order by date and order entry.
			 * 
			 * @param one
			 * @param two
			 * @return
			 */
			@Override
			public int compare(Order one, Order two) {
				 int i = one.getDate().compareTo(two.getDate());
				 if (i != 0){
					 return i;
				 }
				 
				 i = compareEntries(one, two);
				 if (i != 0){
					 return i;
				 }
				 
				 return 0;
			}

		});
		
		for (Order order : allOrders) {

			if (order.isEntry()) {
				
				log.debug( String.format("Order[%s]", order) );

				Position position = order.getParentPosition();
				
				if(position.isQuantityCalculated()){
					// calculate and set the order value based on the quantity calculator
					int quantity = position.getQuantityCalculator().execute(totalCapitalAndEquity, availableCash);
					position.setQuantity(quantity);
					
					double val = position.getEntryPrice() * position.getQuantity();
					order.setValue(val);
					//position.getEntry().setValue(val);
					//position.getExit().setValue(val);
				}
				

				if (order.getValue() > availableCash) {
					log.debug("Not enough available cash to purchase stock - "
							+ position);
					// remove both order sides as position cannot be taken
					position.setSuccessfullyFilled(false);

					continue;

				} else {

					double entryCost = order.getValue()
							+ (order.getValue() * this.slippage)
							+ this.commission;

					availableCash -= entryCost;

					log.debug(order + " Available Cash : " + availableCash);

					onEnterOrder(order, totalCapitalAndEquity, availableCash);
				}

			} else {
				Position position = order.getParentPosition();

				if (position.isSuccessfullyFilled()) {
					
					if(position.isQuantityCalculated()){						
						double val = position.getExitPrice() * position.getQuantity();
						order.setValue(val);
					}

					double profit = (position.getExitPrice() - position
							.getEntryPrice()) * position.getQuantity();
					profit -= (profit * this.slippage);
					profit -= this.commission;

					totalCapitalAndEquity += profit;

					double exitCost = order.getValue()
							- (order.getValue() * this.slippage)
							- this.commission;

					//availableCash = totalCapitalAndEquity;
					availableCash += exitCost;

					log.debug(order + " Available Cash : " + availableCash);

					onExitOrder(order, totalCapitalAndEquity, availableCash);
				}
			}

		}

		/*
		 * for (Position position : openPositions) {
		 * processOpenPosition(position); }
		 */

	}

	public AbstractReport(double capital, double commission, double slippage,
			Collection<Position> closedPositions,
			Collection<Position> openPositions) {
		super();
		this.totalCapitalAndEquity = capital;
		this.availableCash = capital;
		this.commission = commission;
		this.slippage = slippage;
		this.closedPositions = closedPositions;
		this.openPositions = openPositions;
		
	}

	public abstract void onEnterOrder(Order order, double capital,
			double availableCash);

	public abstract void onExitOrder(Order order, double capital,
			double availableCash);

	public abstract void render();

}
