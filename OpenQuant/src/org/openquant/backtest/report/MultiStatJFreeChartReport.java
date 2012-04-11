package org.openquant.backtest.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.openquant.backtest.Order;
import org.openquant.backtest.Position;

public class MultiStatJFreeChartReport extends AbstractReport {
	
	private Log log = LogFactory.getLog(MultiStatJFreeChartReport.class);

	private int iterations;
	
	private String filename;
	
	private List<JFreeChartReport> reports = new ArrayList<JFreeChartReport>();
	
	public MultiStatJFreeChartReport(String filename, double capital, double commission,
			double slippage, List<Position> closedPositions,
			Collection<Position> openPositions, int iterations) {
		super(capital, commission, slippage, closedPositions, openPositions);
		this.iterations = iterations;
		this.filename = filename;
		
		for (int i = 0 ; i < iterations; i++){
			
			ArrayList<Position> copy = new ArrayList<Position>(closedPositions);
			Collections.copy(copy, closedPositions);
			
			Collections.shuffle(copy);
			// order all of the positions by entry date
			Collections.sort(copy, new Comparator<Position>() {

				@Override
				public int compare(Position positionOne, Position positionTwo) {
					return positionOne.getEntryDate().compareTo(
							positionTwo.getEntryDate());
				}

			});
			
			System.out.println(" ===== ");
			for (Position pos : copy){
				System.out.println(pos);
			}
		
			JFreeChartReport report = new JFreeChartReport(filename + ".jpg",
					capital, commission, slippage, copy, openPositions);
			
			report.process();
			reports.add( report );
		
		}
		
		
	}

	@Override
	public void onEnterOrder(Order order, double capital, double availableCash) {
		
	}

	@Override
	public void onExitOrder(Order order, double capital, double availableCash) {

	}

	@Override
	public void render() {
		try {
			// Add the series to your data set
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			
			//int i = 0;
			for(JFreeChartReport report : reports){
				
				//i++;
				//report.getEquitySeries().setKey(i);
				//report.getEquitySeries().setDescription("equity" + i);
				//report.getCashSeries().setDescription("cash" + i);
				
				dataset.addSeries(report.getEquitySeries());
				//dataset.addSeries(report.getCashSeries());
			}

			// Generate the graph
						
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
					"Equity Curve", "Day", "Value", dataset, false, false,
					false);

			ChartUtilities.saveChartAsJPEG(new File(filename), chart, 1000, 600);
			
		} catch (IOException e) {
			log.error(e, e);
		}

	}

}
