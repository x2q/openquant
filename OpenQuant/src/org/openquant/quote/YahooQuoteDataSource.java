package org.openquant.quote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Candle;

public class YahooQuoteDataSource extends QuoteDataSource {

	private Log log = LogFactory.getLog(YahooQuoteDataSource.class);

	private static final String QUOTE_URL = "http://quote.yahoo.com/d/quotes.csv?s=%s&d=t&f=sl1d1t1c1ohgvj1pp2wern";

	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yyyy hh:mmaaa");
	
	public YahooQuoteDataSource(List<String> symbols, GlobalQuoteListener globalListener) {		
		super(symbols, globalListener);
	}
	
	public YahooQuoteDataSource(List<String> symbols){
		this.setSymbols(symbols);
	}
	
	@Override
	public Candle retrieveQuoteCandle(String symbol) {
		String urlStr = String.format(QUOTE_URL, symbol);
		String line = readLine(urlStr);

		Candle returnCandle = null;
		// "MSFT",28.46,"5/7/2010","1:23pm",-0.52,28.90,28.94,27.32,94535576,249.4B,28.98,"-1.79%","19.01 - 31.58",1.93,15.02,"Microsoft Corpora"
		try {
			StringTokenizer str = new StringTokenizer(line, ",\"");
			String sym = str.nextToken();
			double quote = Double.parseDouble(str.nextToken());
			Date date = DATE_FORMAT.parse(str.nextToken() + " " + str.nextToken());
			String change = str.nextToken();
			double open = Double.parseDouble(str.nextToken());
			double high = Double.parseDouble(str.nextToken());
			double low = Double.parseDouble(str.nextToken());
			double volume = Double.parseDouble(str.nextToken());

			returnCandle = new Candle(symbol, date, open, high, low, quote, volume);

		} catch (NumberFormatException e) {
			log.error(e, e);
			//try again
			//retrieveQuoteCandle(symbol);
		} catch (ParseException e) {
			log.error(e, e);
			//try again
			//retrieveQuoteCandle(symbol);
		}
		
		return returnCandle;
	}

	private String readLine(final String urlStr) {
		BufferedReader reader;
		String line = null;

		try {
			URL url = new URL(urlStr);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			line = reader.readLine();
		} catch (MalformedURLException e) {
			log.error(e, e);
		} catch (IOException e) {
			log.error(e, e);
		}

		return line;
	}

	public static void main(String... args) {

		final Log log = LogFactory.getLog(YahooQuoteDataSource.class);

		
		List<String> symbols = new ArrayList<String>();
		symbols.add("MSFT");
		symbols.add("GOOG");
		
		QuoteDataSource quote = new YahooQuoteDataSource(symbols, new GlobalQuoteListener(){
			
			@Override
			public void onGetQuote(Candle quoteCandle) {
				log.info(String.format("Got quote %s[%s, %s, %s, %s, %s, %s]", quoteCandle.getSymbol(),
						quoteCandle.getOpenPrice(), quoteCandle.getHighPrice(), quoteCandle.getLowPrice(),
						quoteCandle.getClosePrice(), quoteCandle.getVolume(), quoteCandle.getDate()));
				
			}
		}
		);

		quote.setLoopForever(true);
		quote.setInterval(1);
		quote.initialize();

	}

	

}
