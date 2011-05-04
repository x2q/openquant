package org.openquant.backtest;

import java.util.Date;

public class Order {
	private boolean entry;

	private Date date;

	private double value;

	private Position parentPosition;

	public Order(boolean entry, Date date, double value, Position position) {
		super();
		this.date = date;
		this.value = value;
		this.entry = entry;
		this.parentPosition = position;
	}

	public Date getDate() {
		return date;
	}

	public boolean isEntry() {
		return entry;
	}

	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}

	public Position getParentPosition() {
		return parentPosition;
	}

	@Override
	public String toString() {
		return "Order [entry=" + entry + ", date=" + date + ", value="
				+ value + "]";
	}

}