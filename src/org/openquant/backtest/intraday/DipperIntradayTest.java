package org.openquant.backtest.intraday;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Position;
import org.openquant.backtest.QuantityCalculator;
import org.openquant.backtest.Series;
import org.openquant.backtest.intraday.IntradayCandle.CANDLETYPE;
import org.openquant.data.SeriesDatasource;

public class DipperIntradayTest extends AbstractIntradayTest {
	
	private static Log log = LogFactory.getLog(DipperIntradayTest.class);
		
	private static final int EXIT_TIME_DAYS = 1;
	
	private static final double MAX_POSITION_AMOUNT = 100000;

	@Override
	public void run() {
			
		Series Closes = dayCloseSeries();

		IntradayCandleSeries daySeries = getCandleSeries();

		for (int bar = 2; bar < daySeries.getDayCount(); bar++) {
			
			//System.out.println( String.format( "bar : %s, date : %s, low : %s, Filter.getAt(bar) : %s", dayIndex, daySeries.getDayCandles(dayIndex).get(0).getDate(),
			//		Lows.getAt(dayIndex), Filter.getAt(dayIndex) ));
			
			final int b = bar;
			
			List<IntradayCandle> dayCandles = daySeries.getDayCandles(bar);
			
			//debugCandles(dayCandles);
			
			double todaysOpen = 0;
			
			for (IntradayCandle candle : dayCandles) {
				
				if (candle.getType().equals(CANDLETYPE.OPEN)) {
					todaysOpen = candle.getOpenPrice();
				}
				
				// tick processing for the order manager
				if (candle.getType().equals(CANDLETYPE.CLOSE)) {
					processClose(candle);
				} else {
					processTick(candle);
				}
				
				// do not open more than one position per stock at a time
				if (!hasOpenPositions()) {

						final double LIMITPRICE = Closes.getAt(bar-1) - ( todaysOpen * 0.08);

						buyAtLimit(LIMITPRICE, new QuantityCalculator() {
							
							@Override
							public int execute(double totalEquity, double availableCash) {
								int quantity = (int) (totalEquity/20 / LIMITPRICE);

								if (quantity * LIMITPRICE > MAX_POSITION_AMOUNT){
									quantity = (int) (MAX_POSITION_AMOUNT / LIMITPRICE);
								}
								return quantity;
							}
						}, new OrderCallback() {

							@Override
							public void success(Position position) {
								
								String candleString = String.format("Candle[%s, %s, %s, %s]", dayOpenSeries().getAt(b), dayHighSeries().getAt(b), dayLowSeries().getAt(b), dayCloseSeries().getAt(b));
								
								System.out.println("ENTER Position : " + candleString + " at limit Price: " + position.getEntryPrice());
								
								timeBasedExitOnClose(EXIT_TIME_DAYS, position, new OrderCallback() {
									
									
									@Override
									public void success(Position position) {
									}
									
									@Override
									public void expired() {
										// TODO Auto-generated method stub
										
									}
								});
								
							}

							@Override
							public void expired() {
								// TODO Auto-generated method stub

							}
						});
						
					
				}
			}
		}

	}

	void debugCandles(List<IntradayCandle> cList){
		
		for(IntradayCandle c : cList){
			System.out.println(c);
		}
		
	}

	public static void main(String ... args){
		
		SeriesDatasource datasource = new IntradaySeriesDatasource("/home/jay/StockDatabase/Test");
		//IntradayCandleSeries series = (IntradayCandleSeries) datasource.fetchSeries("MSFT");
		
		List<String> symbols = new ArrayList<String>();
		
		symbols.add("MSFT");
				
		// nasdaq10
		/*
		symbols.add("ATVI");		
		symbols.add("ADBE");
		symbols.add("AKAM");
		symbols.add("ALTR");
		symbols.add("AMZN");
		symbols.add("AMGN");
		symbols.add("APOL");
		symbols.add("AAPL");
		symbols.add("AMAT");
		symbols.add("ADSK");
		*/
		
		//IntradayBackTestExecutor executor = new IntradayBackTestExecutor("/home/jay/StockDatabase/Test", new DipperIntradayTest(), "IntradayDipper-100");
		
		IntradayBackTestExecutor executor = new IntradayBackTestExecutor(datasource, symbols, new DipperIntradayTest(), "IntradayDipper-MSFT");
		executor.run();
		
	}
}
