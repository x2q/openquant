package org.openquant.backtest.report;

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

public class JFreeChartReportOriginal implements PositionProcessor {
	
	private Log log = LogFactory.getLog(JFreeChartReportOriginal.class);
	
	private double capital;
		
	private double slippage;
	
	private double commission;
		
	private double availableCash;
	
	private String filename;
	
	private TimeSeries series = new TimeSeries("Equity Curve");
	
	public JFreeChartReportOriginal(String filename, double capital, double commission, double slippage) {
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
		
		// calculate entry value and add to capital
		capital += position.getEntryPrice() * position.getQuantity();
		
		//double profit = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();
		//profit = profit - (profit*slippage) - commission;
		
		
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
			
			log.debug(String.format("Ending capital is %12.2f", capital));

		} catch (IOException e) {
			log.error(e, e);
		}

	}

}
