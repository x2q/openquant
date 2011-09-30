package org.openquant.backtest.intraday;

import java.util.ArrayList;
import java.util.List;

import org.openquant.backtest.Position;
import org.openquant.backtest.Series;
import org.openquant.backtest.intraday.IntradayCandle.CANDLETYPE;
import org.openquant.data.SeriesDatasource;

public class MyTest extends AbstractIntradayTest {
	
	static boolean transactedToday = false;

	@Override
	public void run() {

		System.out.println("Beginning test...");
		
		int Len = 18;
		Series Close = dayCloseSeries();
		Series EMAClose = Close.EMA(Len);
		Series DayHighs = dayHighSeries();

		IntradayCandleSeries daySeries = getCandleSeries();

		for (int dayIndex = Len+2; dayIndex < daySeries.getDayCount(); dayIndex++) {

			List<IntradayCandle> dayCandles = daySeries.getDayCandles(dayIndex);
			
			//debugCandles(dayCandles);
			
			transactedToday = false;
			for (IntradayCandle candle : dayCandles) {
				
				// if we buy a stock, hold overnight
				if (transactedToday){
					break;
				}

				// tick processing for the order manager
				if (candle.getType().equals(CANDLETYPE.CLOSE)) {
					processClose(candle);
				} else {
					processTick(candle);
				}
				
				// do not open more than one position per stock at a time
				if (!hasOpenPositions()) {

					//if (EMAClose.getAt(dayIndex - 1) > EMAClose.getAt(dayIndex - 2)) {
					
					//if(candle.getHighPrice() > EMAClose.getAt(dayIndex - 1)){
					if( DayHighs.getAt(dayIndex - 1) > EMAClose.getAt(dayIndex - 2)){
						
						double LIMITPRICE = getClose(dayIndex - 1)
								* (1 - 0.03 * EMAClose.getAt(dayIndex - 1) / 50);

						buyAtLimit(LIMITPRICE, 1000, new OrderCallback() {

							@Override
							public void success(Position position) {
								
								sellAtStop(position.getEntryPrice() * 0.99, position.getQuantity(), position, new OrderCallback() {
									
									@Override
									public void success(Position position) {
										//transactedToday = true;
										
									}
									
									@Override
									public void expired() {
										// TODO Auto-generated method stub
										
									}
								});

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
								
								transactedToday = true;

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
		
		//symbols.add("AAPL");
				
		// nasdaq10
		
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
		
		
		/*
		symbols.add("AAPL");  
		symbols.add("APOL");  
		symbols.add("CERN");  
		symbols.add("DLTR");  
		symbols.add("FLEX");  
		symbols.add("INTU");   
		symbols.add("MICC");  
		symbols.add("NWSA");  
		symbols.add("SBUX");  
		symbols.add("VMED");
		symbols.add("ADBE");  
		symbols.add("ATVI");  
		symbols.add("CHKP");   
		symbols.add("DTV");   
		symbols.add("FLIR");  
		symbols.add("ISRG");   
		symbols.add("MRVL");  
		symbols.add("ORCL");  
		symbols.add("SHLD");  
		symbols.add("VOD");
		symbols.add("ADP");   
		symbols.add("BBBY");  
		symbols.add("CHRW");   
		symbols.add("EBAY");  
		symbols.add("FSLR");  
		symbols.add("JOYG");   
		symbols.add("MSFT");  
		symbols.add("ORLY");  
		symbols.add("SIAL");  
		symbols.add("VRSN");
		symbols.add("ADSK");  
		symbols.add("BIDU");  
		symbols.add("CMCSA");  
		symbols.add("ERTS");  
		symbols.add("GILD");  
		symbols.add("KLAC");   
		symbols.add("MU");    
		symbols.add("PAYX");  
		symbols.add("SNDK");  
		symbols.add("VRTX");
		symbols.add("AKAM");  
		symbols.add("BIIB");  
		symbols.add("COST");   
		symbols.add("ESRX");  
		symbols.add("GOOG");  
		symbols.add("LIFE");   
		symbols.add("MXIM");  
		symbols.add("PCAR");  
		symbols.add("SPLS");  
		symbols.add("WCRX");
		symbols.add("ALTR");  
		symbols.add("BMC");   
		symbols.add("CSCO");   
		symbols.add("EXPD");  
		symbols.add("GRMN");  
		symbols.add("LINTA");  
		symbols.add("MYL");   
		symbols.add("PCLN");  
		symbols.add("SRCL");  
		symbols.add("WFMI");
		symbols.add("ALXN");  
		symbols.add("BRCM");  
		symbols.add("CTRP");   
		symbols.add("EXPE");  
		symbols.add("HSIC");  
		symbols.add("LLTC");   
		symbols.add("NFLX");  
		symbols.add("QCOM");  
		symbols.add("STX");   
		symbols.add("WYNN");
		symbols.add("AMAT");  
		symbols.add("CA");    
		symbols.add("CTSH");   
		symbols.add("FAST");  
		symbols.add("ILMN");  
		symbols.add("LRCX");   
		symbols.add("NIHD");  
		symbols.add("QGEN");  
		symbols.add("SYMC");  
		symbols.add("XLNX");
		symbols.add("AMGN");  
		symbols.add("CELG");  
		symbols.add("CTXS");   
		symbols.add("FFIV");  
		symbols.add("INFY");  
		symbols.add("MAT");    
		symbols.add("NTAP");  
		symbols.add("RIMM");  
		symbols.add("TEVA");  
		symbols.add("XRAY");
		symbols.add("AMZN");  
		symbols.add("CEPH");  
		symbols.add("DELL");   
		symbols.add("FISV");  
		symbols.add("INTC");  
		symbols.add("MCHP");   
		symbols.add("NVDA");  
		symbols.add("ROST");  
		symbols.add("URBN");  
		symbols.add("YHOO");
		*/
		
		IntradayBackTestExecutor executor = new IntradayBackTestExecutor(datasource, symbols, new MyTest(), "Intraday");
		executor.run();
		
	}
}
