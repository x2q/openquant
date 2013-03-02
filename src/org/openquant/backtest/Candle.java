package org.openquant.backtest;

/*
 Copyright (c) 2009, Jay Logelin
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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Candle implements Comparable<Candle>, Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	@Id
	private Integer id;
	
	@SuppressWarnings("unused")
	@Version
	private Object version;

	private String symbol;

	private Date date = new Date();

	private double openPrice;

	private double highPrice;

	private double lowPrice;

	private double closePrice;

	private double volume;

	public Candle() {
	}

	public Candle(String symbol, Date date, double open, double high, double low, double close, double volume) {
		super();
		this.symbol = symbol;
		this.date = date;
		this.openPrice = open;
		this.highPrice = high;
		this.lowPrice = low;
		this.closePrice = close;
		this.volume = volume;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double open) {
		this.openPrice = open;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(double high) {
		this.highPrice = high;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(double low) {
		this.lowPrice = low;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double close) {
		this.closePrice = close;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date time) {
		this.date = time;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return String.format("%s [%.2f, %.2f, %.2f, %.2f] [%s]", symbol, openPrice, highPrice, lowPrice, closePrice, date);
	}

	@Override
	public int compareTo(Candle c) {
		return this.getDate().compareTo(c.getDate());
	}

}
