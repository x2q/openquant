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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.openquant.backtest.Order;
import org.openquant.backtest.Position;

public class JFreeChartReport extends AbstractReport {
	
	private Log log = LogFactory.getLog(JFreeChartReport.class);
	
	private String filename;
	
	private TimeSeries equitySeries;
	
	private TimeSeries cashSeries;

	public JFreeChartReport(String filename, double capital, double commission,
			double slippage, Collection<Position> closedPositions,
			Collection<Position> openPositions) {
		
		super(capital, commission, slippage, closedPositions, openPositions);
		
		Random r = new Random();
		
		equitySeries = new TimeSeries("Equity" + r.nextInt());
		
		cashSeries = new TimeSeries("Cash" + r.nextInt());
		
		this.filename = filename;
		
		process();
	}

	@Override
	public void onEnterOrder(Order order, double capital, double availableCash) {
		cashSeries.addOrUpdate(new Day(order.getDate()), availableCash);
		
	}

	@Override
	public void onExitOrder(Order order, double capital, double availableCash) {
		equitySeries.addOrUpdate(new Day(order.getDate()), capital);
		
		cashSeries.addOrUpdate(new Day(order.getDate()), availableCash);
		
	}
	
	public void render() {
		try {
			// Add the series to your data set
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			dataset.addSeries(equitySeries);
			dataset.addSeries(cashSeries);

			// Generate the graph
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
					"Equity Curve", "Day", "Value", dataset, false, false,
					false);

			ChartUtilities.saveChartAsJPEG(new File(filename), chart, 1000, 600);
			
		} catch (IOException e) {
			log.error(e, e);
		}

	}
	
	public TimeSeries getEquitySeries() {
		return equitySeries;
	}
	
	public TimeSeries getCashSeries() {
		return cashSeries;
	}

	public static void main(String ... args) throws Exception{
		
		DateFormat format = new SimpleDateFormat("dd/MM/yy");
				
		Collection<Position> closedPositions = new ArrayList<Position>();		
		
		closedPositions.add( new Position("TEST", format.parse("20/04/11"), format.parse("21/04/11"), 100, 110, 1000 ) );
		closedPositions.add( new Position("TEST", format.parse("21/04/11"), format.parse("22/04/11"), 100, 110, 1000 ) );
		
		closedPositions.add( new Position("TEST", format.parse("22/04/11"), format.parse("23/04/11"), 100, 110, 1000 ) );
		closedPositions.add( new Position("TEST", format.parse("22/04/11"), format.parse("23/04/11"), 100, 110, 1000 ) );
		closedPositions.add( new Position("TEST", format.parse("22/04/11"), format.parse("23/04/11"), 100, 110, 1000 ) );
		
		closedPositions.add( new Position("TEST", format.parse("23/04/11"), format.parse("24/04/11"), 100, 110, 1000 ) );
		closedPositions.add( new Position("TEST", format.parse("24/04/11"), format.parse("25/04/11"), 100, 110, 1000 ) );
		closedPositions.add( new Position("TEST", format.parse("25/04/11"), format.parse("26/04/11"), 100, 110, 1000 ) );
		closedPositions.add( new Position("TEST", format.parse("26/04/11"), format.parse("27/04/11"), 100, 110, 1000 ) );
				
		Collection<Position> openPositions = new ArrayList<Position>();
		
		new JFreeChartReport("mytest.jpg", 100000, 0, 0.0, closedPositions, openPositions).render();
	}

}
