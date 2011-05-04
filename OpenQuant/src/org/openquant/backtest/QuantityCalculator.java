package org.openquant.backtest;

public interface QuantityCalculator {
	
	public int execute(double totalEquity, double availableCash);

}
