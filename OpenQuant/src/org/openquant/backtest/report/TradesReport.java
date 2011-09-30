package org.openquant.backtest.report;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Order;
import org.openquant.backtest.Position;

public class TradesReport extends AbstractReport {
	
	private Log log = LogFactory.getLog(TradesReport.class);
	
	private PrintWriter outputStream;

	public TradesReport(double capital, double commission, double slippage, Collection<Position> closedPositions,
			Collection<Position> openPositions, String filename) {
		super(capital, commission, slippage, closedPositions, openPositions);
		
		try {
			outputStream = new PrintWriter(new FileWriter(filename));
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		process();
	}

	@Override
	public void onEnterOrder(Order order, double capital, double availableCash) {
		outputStream.println( String.format("Entering %s, capital: %.2f, availableCash: %.2f", order, capital, availableCash) );

	}

	@Override
	public void onExitOrder(Order order, double capital, double availableCash) {
		outputStream.println( String.format("Exiting %s, capital: %.2f, availableCash: %.2f", order, capital, availableCash) );
	}

	@Override
	public void render() {
		
		if (outputStream != null) {
            outputStream.close();
        }

	}

}
