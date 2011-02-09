package org.openquant.backtest.report;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.openquant.backtest.Position;
import org.openquant.backtest.PositionProcessor;

public class JFreeChartReport implements PositionProcessor {
	
	private Log log = LogFactory.getLog(JFreeChartReport.class);
	
	private double capital;
		
	private double slippage;
	
	private double commission;
	
	private Date currentDate = new Date();
	
	private String filename;
	
	private TimeSeries series = new TimeSeries("Equity Curve");
	
	public JFreeChartReport(String filename, double capital, double commission, double slippage) {
		super();
		this.capital = capital;
		this.slippage = slippage;
		this.commission = commission;
		this.filename = filename;
				
	}

	@Override
	public void processClosePosition(Position position) {
		
		double profit = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();
		profit = profit - (profit*slippage) - commission;
		
		capital += profit;		
		
		series.addOrUpdate(new Day(position.getExitDate()), capital);

	}

	@Override
	public void processOpenPosition(Position position) {
		
		capital -= position.getEntryPrice() * position.getQuantity();
		
		double profit = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();
		profit = profit - (profit*slippage) - commission;
		
		series.addOrUpdate(new Day( new Date() ), capital);

	}
	
	@Override
	public void finish() {
		try {
			// Add the series to your data set
			TimeSeriesCollection dataset = new TimeSeriesCollection(series);

			// Generate the graph
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
					"Equity Curve", "Day", "Value", dataset, false, false,
					false);

			ChartUtilities.saveChartAsJPEG(new File(filename), chart, 1000, 600);
			
			log.info(String.format("Ending capital is %12.2f", capital));

		} catch (IOException e) {
			log.error(e, e);
		}

	}

}
