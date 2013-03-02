package org.openquant.backtest;

/*
 Copyright (c) 2010, Jay Logelin
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.report.AbstractReport;
import org.openquant.backtest.report.CSVReport;
import org.openquant.backtest.report.JFreeChartReport;
import org.openquant.backtest.report.TradesReport;
import org.openquant.data.SeriesDatasource;
import org.springframework.util.StopWatch;

public class OrderedSignalBackTestExecutor {

	private Log log = LogFactory.getLog(OrderedSignalBackTestExecutor.class);

	private SeriesDatasource data;

	private List<String> symbols;

	private CandleSeriesTestContext test;

	private double capital = 100000;

	private double commission = 9.99;

	private double slippage = 0.001;

	private List<Position> positions = new ArrayList<Position>();

	private String reportName;
	
	private int testCycles = 1;
	
	public int getTestCycles() {
		return testCycles;
	}

	public void setTestCycles(int testCycles) {
		this.testCycles = testCycles;
	}

	public OrderedSignalBackTestExecutor(SeriesDatasource data, List<String> symbols,
			CandleSeriesTestContext test, String reportName, double capital,
			double commission, double slippage) {
		super();
		this.data = data;
		this.symbols = symbols;
		this.test = test;
		this.capital = capital;
		this.commission = commission;
		this.slippage = slippage;
		this.reportName = reportName;
		
	}
	
	Map<String, CandleSeries> seriesCache = new HashMap<String, CandleSeries>();

	private void populateCache(){
		for (String symbol : symbols) {
			
			try {
				seriesCache.put(symbol, data.fetchSeries(symbol) );
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			
		}
	}
	
	public double run() {

		StopWatch watch = new StopWatch("-- DEBUGGING --");
		watch.start("execute tradesystem");
		
		Collections.shuffle(symbols);

		for (String symbol : symbols) {

			CandleSeries candleSeries;
			try {
				candleSeries = data.fetchSeries(symbol);
		
				test.reset();
				test.setSeries(candleSeries);
				test.run();

				positions.addAll(test.getOrderManager().getClosedPositions());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}

		// order all of the positions by entry date
		Collections.sort(positions, new Comparator<Position>() {

			@Override
			public int compare(Position positionOne, Position positionTwo) {
				return positionOne.getEntryDate().compareTo(
						positionTwo.getEntryDate());
			}

		});
		
		int posSize = test.getOrderManager().getOpenPositions().size();
		
		log.info("Open Position Size : " + posSize);

		// generate reports
		AbstractReport report = new JFreeChartReport(reportName + ".jpg",
				capital, commission, slippage, positions, test
						.getOrderManager().getOpenPositions());
		//double endingCapital = report.getTotalCapitalAndEquity();
		//log.debug(String.format("Ending capital is %12.2f", endingCapital));
		report.render();
		
		new CSVReport(capital, commission, slippage, positions, test
				.getOrderManager().getOpenPositions(), reportName + ".csv").render();

		new TradesReport(capital, commission, slippage, positions, test
				.getOrderManager().getOpenPositions(), reportName + "-trades.txt").render();
		

		watch.stop();
		log.debug(String.format("Time : %s seconds", watch.getTotalTimeSeconds()));

		return report.getTotalCapitalAndEquity();
	}


}