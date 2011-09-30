package org.openquant.backtest.intraday;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquant.backtest.Candle;
import org.openquant.backtest.CandleSeries;
import org.openquant.backtest.Series;
import org.openquant.backtest.intraday.IntradayCandle.CANDLETYPE;
import org.openquant.util.Day;

public class IntradayCandleSeries extends CandleSeries {

	private static final long serialVersionUID = 1L;

	private Series dayCloseSeries;

	private Series dayHighSeries;
	
	private Series dayLowSeries;
	
	private Series dayOpenSeries;
	
	private List<List<IntradayCandle>> dayCandles = new ArrayList<List<IntradayCandle>>();

	public IntradayCandleSeries(List<Candle> candles) {
		super(candles);

		processCandles();
	}
	
	public int getDayCount(){
		return dayCandles.size();
	}
	
	public List<IntradayCandle> getDayCandles(int dayIndex){
		return dayCandles.get(dayIndex);
	}

	private void processCandles() {

		List<Double> closes = new ArrayList<Double>();
		List<Double> opens = new ArrayList<Double>();
		
		List<Double> highs = new ArrayList<Double>();
		List<Double> lows = new ArrayList<Double>();

		IntradayCandle previous = null;
		
		List<IntradayCandle> day = new ArrayList<IntradayCandle>();
		
		double dayLow = 0;
		double dayHigh = 0;
		
		for (Candle can : candles) {
			
			IntradayCandle candle = (IntradayCandle)can;
			
			if (previous == null) {
				
				dayLow = candle.getLowPrice();
				dayHigh = candle.getHighPrice();
								
				opens.add(candle.getOpenPrice());
				previous = candle;
				continue;
			}			

			if (Day.compare(candle.getDate(), previous.getDate()) != 0) {
				// another day
				// mark the candles as open and close candles
				
				previous.setType(CANDLETYPE.CLOSE);
				candle.setType(CANDLETYPE.OPEN);
								
				closes.add(previous.getClosePrice());
				opens.add(candle.getOpenPrice());
				highs.add(dayHigh);
				lows.add(dayLow);
				dayLow = candle.getLowPrice();
				dayHigh = candle.getHighPrice();
				
				
				dayCandles.add(day);
				day = new ArrayList<IntradayCandle>();

			}
			
			// check the highs/lows
			if (candle.getLowPrice() < dayLow){
				dayLow = candle.getLowPrice();
			}
			if (candle.getHighPrice() > dayHigh){
				dayHigh = candle.getHighPrice();
			}
			
			day.add(candle);
			
			previous = candle;
					
		}

		dayCloseSeries = new Series(closes);
		dayOpenSeries = new Series(opens);
		dayHighSeries = new Series(highs);
		dayLowSeries = new Series(lows);
	}

	public Series getDayHighSeries() {
		return dayHighSeries;
	}

	public Series getDayLowSeries() {
		return dayLowSeries;
	}

	public Series getDayOpenSeries() {
		return dayOpenSeries;
	}

	public Series getDayCloseSeries() {
		return dayCloseSeries;
	}
}
