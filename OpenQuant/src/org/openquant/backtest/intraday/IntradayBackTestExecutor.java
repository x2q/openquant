package org.openquant.backtest.intraday;

/*
 Copyright (c) 2011, Jay Logelin
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Position;
import org.openquant.backtest.report.AbstractReport;
import org.openquant.backtest.report.CSVReport;
import org.openquant.backtest.report.JFreeChartReport;
import org.openquant.backtest.report.TradesReport;
import org.openquant.data.SeriesDatasource;
import org.springframework.util.StopWatch;

public class IntradayBackTestExecutor {

	private Log log = LogFactory.getLog(IntradayBackTestExecutor.class);

	private SeriesDatasource data;

	private List<String> symbols;

	private AbstractIntradayTest test;

	private double capital = 100000;

	private double commission = 9.99;

	private double slippage = 0.001;

	private List<Position> positions = new ArrayList<Position>();

	private String reportName;

	public IntradayBackTestExecutor(SeriesDatasource data, List<String> symbols,
			AbstractIntradayTest test, String reportName, double capital,
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
	
	public IntradayBackTestExecutor(SeriesDatasource data, List<String> symbols,
			AbstractIntradayTest test, String reportName){
		super();
		this.data = data;
		this.symbols = symbols;
		this.test = test;
		this.reportName = reportName;
	}
	
	public IntradayBackTestExecutor(String symbolsDirectory, AbstractIntradayTest test, String reportName){
		super();
		data = new IntradaySeriesDatasource(symbolsDirectory);
		populateSymbolsFromDirectory(symbolsDirectory);
		this.test = test;
		this.reportName = reportName;
	}
	
	private void populateSymbolsFromDirectory(String directoryName){
		
		symbols = new ArrayList<String>();
		
		File dir = new File(directoryName);

		String[] children = dir.list();
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i];
		        String stockName = filename.substring(0, filename.lastIndexOf('.'));
		        symbols.add(stockName);
		    }
		}

		
	}

	public double run() {

		StopWatch watch = new StopWatch("-- DEBUGGING --");
		watch.start("execute intraday tradesystem");

		for (String symbol : symbols) {

			IntradayCandleSeries candleSeries;
			try {
				candleSeries = (IntradayCandleSeries) data.fetchSeries(symbol);
				test.reset();
				test.setCandleSeries(candleSeries);
				test.run();

				positions.addAll(test.getOrderManager().getClosedPositions());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally{
				// clean up for gc
				//candleSeries = null;
			}

		}
		
		int posSize = test.getOrderManager().getOpenPositions().size();
		
		log.info("Open Position Size : " + posSize);

		// order all of the positions by entry date
		Collections.sort(positions, new Comparator<Position>() {

			@Override
			public int compare(Position positionOne, Position positionTwo) {
				return positionOne.getEntryDate().compareTo(
						positionTwo.getEntryDate());
			}

		});

		// generate reports
		
		AbstractReport report = new JFreeChartReport(reportName + ".jpg",
				capital, commission, slippage, positions, test
						.getOrderManager().getOpenPositions());
		double endingCapital = report.getTotalCapitalAndEquity();
		log.info(String.format("Ending capital is %12.2f", endingCapital));
		report.render();
		
		
		CSVReport report2 = new CSVReport(capital, commission, slippage, positions, test
				.getOrderManager().getOpenPositions(), reportName + ".csv");
		report2.render();
		
		new TradesReport(capital, commission, slippage, positions, test
				.getOrderManager().getOpenPositions(), reportName + "-trades.txt").render();

		watch.stop();
		log.debug(String.format("Time : %s seconds", watch.getTotalTimeSeconds()));

		return 0;
	}


}