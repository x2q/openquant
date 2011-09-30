package org.openquant.backtest.intraday;

import org.openquant.backtest.Position;

public interface OrderCallback{
	
	public void success(Position position);
	
	public void expired();
	
}