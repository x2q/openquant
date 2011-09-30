package org.openquant.backtest.intraday;

import java.util.ArrayList;
import java.util.List;

import org.openquant.backtest.Position;
import org.openquant.backtest.QuantityCalculator;
import org.openquant.backtest.Series;
import org.openquant.backtest.intraday.IntradayCandle.CANDLETYPE;
import org.openquant.data.SeriesDatasource;

public class LLHIntradayTest extends AbstractIntradayTest {
		
	private static final int EXIT_TIME_DAYS = 12;
	
	private static final double MAX_POSITION_AMOUNT = 100000;

	@Override
	public void run() {
		

		System.out.println("Beginning test...");
		
		int Len = 21;
		Series Lows = dayLowSeries();

		Series Filter = Lows.Min( Len );

		IntradayCandleSeries daySeries = getCandleSeries();

		for (int dayIndex = Len+2; dayIndex < daySeries.getDayCount(); dayIndex++) {
			
			//System.out.println( String.format( "bar : %s, date : %s, low : %s, Filter.getAt(bar) : %s", dayIndex, daySeries.getDayCandles(dayIndex).get(0).getDate(),
			//		Lows.getAt(dayIndex), Filter.getAt(dayIndex) ));
			

			List<IntradayCandle> dayCandles = daySeries.getDayCandles(dayIndex);
			
			//debugCandles(dayCandles);
			
			for (IntradayCandle candle : dayCandles) {
				
				// tick processing for the order manager
				if (candle.getType().equals(CANDLETYPE.CLOSE)) {
					processClose(candle);
				} else {
					processTick(candle);
				}
				
				// do not open more than one position per stock at a time
				if (!hasOpenPositions()) {

						final double LIMITPRICE = Filter.getAt(dayIndex);

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
																
								sellAtLimit(position.getEntryPrice() * 1.025, position.getQuantity(), position, new OrderCallback() {
									
									@Override
									public void success(Position position) {
										//transactedToday = true;
										
									}
									
									@Override
									public void expired() {
										// TODO Auto-generated method stub
										
									}
								});
																
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
		
		//IntradayBackTestExecutor executor = new IntradayBackTestExecutor("/home/jay/StockDatabase/Test", new LLHIntradayTest(), "Intraday-100");
		
		IntradayBackTestExecutor executor = new IntradayBackTestExecutor(datasource, symbols, new LLHIntradayTest(), "Intraday-MSFT");
		executor.run();
		
	}
}
