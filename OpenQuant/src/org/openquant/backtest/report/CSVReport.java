package org.openquant.backtest.report;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Position;
import org.openquant.backtest.PositionProcessor;

public class CSVReport implements PositionProcessor {
	
	private Log log = LogFactory.getLog(CSVReport.class);
	
	private PrintWriter outputStream;
	
	private double capital;
	
	private double slippage;
	
	private double commission;
		
	public CSVReport(String filename, double capital, double commission, double slippage) {
		super();
		this.capital = capital;
		this.slippage = slippage;
		this.commission = commission;
		
		try {
			outputStream = new PrintWriter(new FileWriter(filename));
			
			outputStream.println( String.format("%s,%s,%s,%s,%s,%s,%s,%s", 
					"Symbol","Entry Date", "Entry Price", "Exit Date", "Exit Price", "Shares", "Profit", "Equity" ) );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void processClosePosition(Position position) {		
		
		double profit = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();
		profit = profit - (profit*slippage) - commission;
		
		capital += profit;		
						
		outputStream.println( String.format("%1$s,%2$tb %2$td %2$ty,%3$6.2f,%4$tb %4$td %4$ty,%5$6.2f,%6$s,%7$6.2f,%8$6.2f", position.getSymbol(), position.getEntryDate(),
				position.getEntryPrice(), position.getExitDate(), position.getExitPrice(), position.getQuantity(), profit, capital ) );

	}

	@Override
	public void processOpenPosition(Position position) {
		capital -= position.getEntryPrice() * position.getQuantity();
		
		double profit = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();
		profit = profit - (profit*slippage) - commission;
		
		outputStream.println( String.format("%1$s,%2$tb %2$td %2$ty,%3$6.2f,%4$tb %4$td %4$ty,%5$6.2f,%6$s,%7$6.2f,%8$6.2f", position.getSymbol(), position.getEntryDate(),
				position.getEntryPrice(), position.getExitDate(), position.getExitPrice(), position.getQuantity(), profit, capital ) );

	}
	
	@Override
	public void finish() {
		if (outputStream != null) {
            outputStream.close();
        }
		
		log.info(String.format("Ending capital is %12.2f", capital));

	}

}
