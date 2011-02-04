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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.report.CSVReport;
import org.openquant.data.SeriesDatasource;
import org.springframework.util.StopWatch;

public class BackTestExecutor {

	private Log log = LogFactory.getLog(BackTestExecutor.class);

	private SeriesDatasource data;

	private List<String> symbols;

	private CandleSeriesTestContext test;

	private double capital = 100000;

	private double commission = 9.99;

	private double slippage = 0.2;

	private List<Position> positions = new ArrayList<Position>();

	private List<PositionProcessor> processors = new ArrayList<PositionProcessor>();

	public BackTestExecutor(SeriesDatasource data, List<String> symbols, CandleSeriesTestContext test, double capital,
			double commission, double slippage) {
		super();
		this.data = data;
		this.symbols = symbols;
		this.test = test;
		this.capital = capital;
		this.commission = commission;
		this.slippage = slippage;
	}

	public List<PositionProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<PositionProcessor> processors) {
		this.processors = processors;
	}

	public void run() {

		StopWatch watch = new StopWatch("-- DEBUGGING --");
		watch.start("execute tradesystem");

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
				return positionOne.getEntryDate().compareTo(positionTwo.getEntryDate());
			}

		});

		List<Position> activePositions = equityFilterPositions(test.getOrderManager().getOpenPositions());

		// add a built in equity calculator
		// processors.add(new EquityCalculator());

		processPositions(activePositions, test.getOrderManager().getOpenPositions());

		// calculateTotalEquity(test.getOrderManager().getOpenPositions());

		watch.stop();
		log.info(String.format("Time : %s seconds", watch.getTotalTimeSeconds()));
	}

	private List<Position> equityFilterPositions(Set<Position> openPositions) {

		EquityCalculator calculator = new EquityCalculator(capital, commission, slippage);

		for (Position position : positions) {
			calculator.processClosePosition(position);
		}

		for (Position position : openPositions) {
			calculator.processOpenPosition(position);
		}

		calculator.finish();
		
		return calculator.getPositions();

	}

	private class EquityCalculator implements PositionProcessor {

		private Log log = LogFactory.getLog(EquityCalculator.class);

		private double capital;

		private double availableCapital;

		private double slippage;

		private double commission;

		private Date currentDate = new Date();

		private List<Position> positions = new ArrayList<Position>();

		public EquityCalculator(double capital, double commission, double slippage) {
			super();
			this.capital = capital;
			this.slippage = slippage;
			this.commission = commission;
			this.availableCapital = capital;
		}

		public List<Position> getPositions() {
			return positions;
		}

		private double lastPositionSize = 0;

		@Override
		public void processClosePosition(Position position) {

			if (position.getEntryDate().equals(currentDate)) {
				// same date
				availableCapital -= lastPositionSize;

				double entryCost = position.getEntryPrice() * position.getQuantity();

				if (entryCost > availableCapital) {

					log.debug(String.format("Not enough available capital (%s) for entry order worth %s[%s]",
							availableCapital, entryCost, position));
					return;

				}

			} else {
				// different date
				currentDate = position.getEntryDate();
				//availableCapital = capital;

				double entryCost = position.getEntryPrice() * position.getQuantity();
				if (entryCost > capital) {

					log.debug(String.format("Not enough capital (%s) for entry order worth %s[%s]", availableCapital,
							entryCost, position));
					return;

				}

			}

			lastPositionSize = position.getEntryPrice() * position.getQuantity();

			double profit = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();
			profit = profit - (profit * slippage) - commission;

			capital += profit;
			availableCapital += profit;

			positions.add(position);

		}

		@Override
		public void processOpenPosition(Position position) {
			capital -= position.getEntryPrice() * position.getQuantity();

			double profit = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();
			profit = profit - (profit * slippage) - commission;

		}

		@Override
		public void finish() {
			log.info(String.format("Ending capital is %12.2f", capital));
		}

	}

	private void processPositions(List<Position> activePositions, Set<Position> openPositions) {

		for (Position position : activePositions) {

			for (PositionProcessor processor : processors) {
				processor.processClosePosition(position);
			}

		}

		for (Position position : openPositions) {
			for (PositionProcessor processor : processors) {
				processor.processOpenPosition(position);
			}
		}

		for (PositionProcessor processor : processors) {
			processor.finish();
		}
	}

	private double calculateCapitalGain(double capital, double commission, double percentSlippage, Position position) {

		double gain = (position.getExitPrice() - position.getEntryPrice()) * position.getQuantity();

		double totalCapital = capital + gain - (gain * percentSlippage) - commission;

		if (gain > 0) {
			log.debug(String.format("PROFIT: %6.2f %12.2f", gain, totalCapital));
		} else {
			log.debug(String.format("LOSS: %6.2f %12.2f", gain, totalCapital));
		}

		return totalCapital;

	}

}
