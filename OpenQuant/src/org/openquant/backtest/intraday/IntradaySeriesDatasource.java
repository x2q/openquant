package org.openquant.backtest.intraday;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.openquant.backtest.Candle;
import org.openquant.backtest.CandleSeries;
import org.openquant.data.SeriesDatasource;

public class IntradaySeriesDatasource implements SeriesDatasource {
	
	public static final String DATE_TIME_FORMAT = "MM/dd/yyyyHHmm";
	
	private final static DateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
	
	private String baseDirectory;
	
	public IntradaySeriesDatasource(String baseDirectory){
		this.baseDirectory = baseDirectory;
	}

	@Override
	public CandleSeries fetchSeries(String symbol) throws Exception {
		
		System.out.println( "Fetching series " + symbol );
		
		List<Candle> candles = new ArrayList<Candle>();		
		
		String strFile = baseDirectory + File.separator + symbol + ".txt";

		// create BufferedReader to read csv file
		BufferedReader br = new BufferedReader(new FileReader(strFile));
		String strLine = "";
		StringTokenizer st = null;
		
		/*
		    "Date","Time","Open","High","Low","Close","Volume"
			12/30/2002,0931,23.85,23.89,23.81,23.88,511498
		 */

		br.readLine();
		// read comma separated file line by line
		while ((strLine = br.readLine()) != null) {

			// break comma separated line using ","
			st = new StringTokenizer(strLine, ",");
			
			String dateString = st.nextToken();
			String timeString = st.nextToken();
			
			Date date = sdf.parse(dateString + timeString);
			
			double open = Double.parseDouble(st.nextToken());
			double high = Double.parseDouble(st.nextToken());
			double low = Double.parseDouble(st.nextToken());
			double close = Double.parseDouble(st.nextToken());
			double volume = Double.parseDouble(st.nextToken());
				
			candles.add(new IntradayCandle(symbol, date, open, high, low, close, volume));		

		}
		
		//System.out.println( "finished." );
		
		return new IntradayCandleSeries(candles);
		
	}
	
	public static void main(String ... args) throws Exception{
		SeriesDatasource datasource = new IntradaySeriesDatasource("/home/jay/StockDatabase/Test");
		IntradayCandleSeries series = (IntradayCandleSeries) datasource.fetchSeries("MSFT");
		
		
		//System.out.print(series);
	}

}
