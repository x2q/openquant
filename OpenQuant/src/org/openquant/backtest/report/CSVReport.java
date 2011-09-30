package org.openquant.backtest.report;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Order;
import org.openquant.backtest.Position;

public class CSVReport extends AbstractReport {
	
	private Log log = LogFactory.getLog(CSVReport.class);
	
	private PrintWriter outputStream;

	public CSVReport(double capital, double commission, double slippage, Collection<Position> closedPositions,
			Collection<Position> openPositions, String filename) {
		super(capital, commission, slippage, closedPositions, openPositions);
		
		try {
			outputStream = new PrintWriter(new FileWriter(filename));
			
			outputStream.println( String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
					"Symbol","Entry Date", "Entry Price", "Exit Date", "Exit Price", "Shares", "Available Cash", "Equity", "% Gain", "Comments" ) );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		process();
	}

	@Override
	public void onEnterOrder(Order order, double capital, double availableCash) {
		/*
		Position position = order.getParentPosition();
		outputStream.println( String.format("%1$s,%2$tb %2$td %2$ty %2$tT,%3$6.2f,%4$tb %4$td %4$ty %4$tT,%5$6.2f,%6$s,%7$6.2f,%8$6.2f,%9$s", position.getSymbol(), position.getEntryDate(),
				position.getEntryPrice(), position.getExitDate(), position.getExitPrice(), position.getQuantity(), availableCash, capital, position.getComments() ) );
		*/
		//log.info( String.format("Entering %s, capital: %.2f, availableCash: %.2f", order, capital, availableCash) );

	}

	@Override
	public void onExitOrder(Order order, double capital, double availableCash) {
		Position position = order.getParentPosition();
		
		double diff = (position.getExitPrice() - position.getEntryPrice())/ position.getEntryPrice() * 100;
		
		outputStream.println( String.format("%1$s,%2$tb %2$td %2$ty %2$tT,%3$6.2f,%4$tb %4$td %4$ty %4$tT,%5$6.2f,%6$s,%7$6.2f,%8$6.2f,%9$6.4f,%10$s", position.getSymbol(), position.getEntryDate(),
				position.getEntryPrice(), position.getExitDate(), position.getExitPrice(), position.getQuantity(), availableCash, capital, diff, position.getComments() ) );

		//log.info( String.format("Exiting %s, capital: %.2f, availableCash: %.2f", order, capital, availableCash) );
	}

	@Override
	public void render() {
		if (outputStream != null) {
            outputStream.close();
        }

	}

}
