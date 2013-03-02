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

public class LowestLowIntra extends AbstractIntradayTest {
	
	private Log log = LogFactory.getLog(LowestLowIntra.class);

	private static boolean transactedToday = false;
	
	private static final double MAX_POSITION_AMOUNT = 100000;

	@Override
	public void run() {

		//System.out.println("Beginning test...");

		int Len = 9;
		Series Filter = dayLowSeries().Min(Len);
		//Series Highs = dayHighSeries();
		//Series Closes = dayCloseSeries();

		IntradayCandleSeries daySeries = getCandleSeries();

		for (int dayIndex = Len; dayIndex < daySeries.getDayCount(); dayIndex++) {
			
			//System.out.println( String.format("%s Close : %.2f Filter[bar-1] : %.2f", daySeries.getDayCandles(dayIndex).get(0).getDate(), Closes.getAt(dayIndex), Filter.getAt(dayIndex - 1) * 1.21)  );

			List<IntradayCandle> dayCandles = daySeries.getDayCandles(dayIndex);

			// debugCandles(dayCandles);

			for (IntradayCandle candle : dayCandles) {

				// tick processing for the order manager
				if (candle.getType().equals(CANDLETYPE.CLOSE)) {
					processClose(candle);
				} else {
					processTick(candle);
				}

				// do not open more than one position per stock at a time
				//if ( getOpenPositionCount() < 20 ){
				if (!hasOpenPositions()) {

					//if (Filter.getAt(dayIndex - 1) > Filter.getAt(dayIndex - 2)) {
					//if (Highs.getAt(dayIndex - 1) > Filter.getAt(dayIndex - 2)) {
					//if (candle.getHighPrice() > Filter.getAt(dayIndex - 1)) {

						final double LIMITPRICE = Filter.getAt(dayIndex - 1) * 1.21;
						
						//final double LIMITPRICE = Filter.getAt(dayIndex - 1);

						buyAtLimit(LIMITPRICE, new QuantityCalculator() {
							
							@Override
							public int execute(double totalEquity, double availableCash) {
								int quantity = (int) (totalEquity/10 / LIMITPRICE);
								
								if (quantity * LIMITPRICE > MAX_POSITION_AMOUNT){
									quantity = (int) (MAX_POSITION_AMOUNT / LIMITPRICE);
								}
								
								return quantity;
							}
						},
						new OrderCallback() {

							@Override
							public void success(Position position) {
								
								//log.info("Entered Position : " + position);
								
								/*
								timeBasedSellAtStop(1, position.getEntryPrice() * 0.80, position.getQuantity(), position,
										new OrderCallback() {

											@Override
											public void success(Position position) {
												// transactedToday = true;

											}

											@Override
											public void expired() {
												// TODO Auto-generated method
												// stub

											}
										});
										
									*/	
								
								timeBasedExitOnClose(12, position, new OrderCallback() {
									
									@Override
									public void success(Position position) {
										//log.info("Exited Position[ time based ] : " + position);
										
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

					//}

				}
			}
		}

	}

	void debugCandles(List<IntradayCandle> cList) {

		for (IntradayCandle c : cList) {
			System.out.println(c);
		}

	}

	public static void main(String... args) {

		SeriesDatasource data = new IntradaySeriesDatasource("/home/jay/StockDatabase/Test");
		// IntradayCandleSeries series = (IntradayCandleSeries)
		// datasource.fetchSeries("MSFT");

		List<String> symbols = new ArrayList<String>();

		//symbols.add("MSFT");

		// nasdaq10
		
		  symbols.add("ATVI"); symbols.add("ADBE"); symbols.add("AKAM");
		  symbols.add("ALTR"); symbols.add("AMZN"); symbols.add("AMGN");
		  symbols.add("APOL"); symbols.add("AAPL"); symbols.add("AMAT");
		  symbols.add("ADSK");
		 
		 

		//IntradayBackTestExecutor executor = new IntradayBackTestExecutor("/home/jay/StockDatabase/Test", new LowestLowIntra(),
		//		"LL4-NAS-100-PosSize");
		
		 IntradayBackTestExecutor executor = new IntradayBackTestExecutor(data, symbols, new LowestLowIntra(), "LL4-Nasdaq10-PosSize");
		  
		  executor.run();

	}
}
