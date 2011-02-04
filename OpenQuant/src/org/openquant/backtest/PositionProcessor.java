package org.openquant.backtest;

public interface PositionProcessor {
	
	public void processClosePosition(final Position position);
	
	public void processOpenPosition(final Position position);
	
	public void finish();

}
