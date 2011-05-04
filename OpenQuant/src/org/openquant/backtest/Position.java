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

import java.util.Date;

public class Position {

	private String symbol;

	private Date entryDate;

	private Date exitDate;

	private double entryPrice;

	private double exitPrice;

	private int quantity;
	
	private String comments;
	
	private double score = 0.0;
	
	private boolean successfullyFilled = true;
	
	private QuantityCalculator quantityCalculator;
	
	private Order entry;
	
	private Order exit;

	public Position() {
	}
	
	public Position(String symbol, Date entryDate, Date exitDate,
			double entryPrice, double exitPrice, int quantity) {
		super();
		this.symbol = symbol;
		this.entryDate = entryDate;
		this.exitDate = exitDate;
		this.entryPrice = entryPrice;
		this.exitPrice = exitPrice;
		this.quantity = quantity;
	}
	
	public boolean isQuantityCalculated(){
		return quantityCalculator != null;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Date getExitDate() {
		return exitDate;
	}

	public void setExitDate(Date exitDate) {
		this.exitDate = exitDate;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}
	
	public void appendComment(String comment){
		this.comments += ", " + comment;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}
	
	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}
	
	public boolean isSuccessfullyFilled() {
		return successfullyFilled;
	}

	public void setSuccessfullyFilled(boolean successfullyFilled) {
		this.successfullyFilled = successfullyFilled;
	}

	public QuantityCalculator getQuantityCalculator() {
		return quantityCalculator;
	}

	public void setQuantityCalculator(QuantityCalculator quantityCalculator) {
		this.quantityCalculator = quantityCalculator;
	}
	
	public Order getEntry() {
		return entry;
	}

	public void setEntry(Order entry) {
		this.entry = entry;
	}

	public Order getExit() {
		return exit;
	}

	public void setExit(Order exit) {
		this.exit = exit;
	}

	@Override
	public String toString() {

		return String.format(
				"{%1$s} entry[%2$td %2$tb %2$ty] : %3$3.2f, exit[%4$td %4$tb %4$ty]: %5$3.2f, quantity: %6$s comments: %7$s score: %8$s", symbol,
				entryDate, entryPrice, exitDate, exitPrice, quantity, comments, score);
	}

	
}
