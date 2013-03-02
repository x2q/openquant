package org.openquant.backtest.intraday;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Candle;
import org.openquant.quote.QuoteDataSource;
import org.openquant.quote.YahooQuoteDataSource;

public class IntradayQuoteDatasource extends QuoteDataSource {

	List<Candle> orderedQuotes = new ArrayList<Candle>();

	public IntradayQuoteDatasource(String directory, Set<String> symbols,
			GlobalQuoteListener globalListener) {
		super(symbols, globalListener);
		initializeSymbolIterators(directory, symbols);
	}

	private void initializeSymbolIterators(String directory, Set<String> symbols) {

		for (String symbol : symbols) {

			try {

				// csv file containing data
				String strFile = directory + File.separator + symbol + ".txt";

				// create BufferedReader to read csv file
				BufferedReader br = new BufferedReader(new FileReader(strFile));
				String strLine = "";
				StringTokenizer st = null;
				int lineNumber = 0, tokenNumber = 0;

				// read comma separated file line by line
				while ((strLine = br.readLine()) != null) {
					lineNumber++;

					// break comma separated line using ","
					st = new StringTokenizer(strLine, ",");

					while (st.hasMoreTokens()) {
						// display csv values
						tokenNumber++;
						System.out.println("Line # " + lineNumber
								+ ", Token # " + tokenNumber + ", Token : "
								+ st.nextToken());
					}

					// reset token number
					tokenNumber = 0;

				}

			} catch (Exception e) {
				System.out.println("Exception while reading csv file: " + e);
			}

		}

		Collections.sort(orderedQuotes);

	}

	@Override
	public Candle retrieveQuoteCandle(String symbol) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String... args) {

		final Log log = LogFactory.getLog(YahooQuoteDataSource.class);

		Set<String> symbols = new HashSet<String>();
		symbols.add("MSFT");
		symbols.add("ORCL");

		QuoteDataSource quote = new IntradayQuoteDatasource(
				"/home/jay/StockDatabase/Test", symbols,
				new GlobalQuoteListener() {

					@Override
					public void onQuote(Candle quoteCandle) {
						log.info(String.format(
								"Got quote %s[%s, %s, %s, %s, %s, %s]",
								quoteCandle.getSymbol(),
								quoteCandle.getOpenPrice(),
								quoteCandle.getHighPrice(),
								quoteCandle.getLowPrice(),
								quoteCandle.getClosePrice(),
								quoteCandle.getVolume(), quoteCandle.getDate()));

					}

					@Override
					public void onOpenQuote(Candle quoteCandle) {
						log.info(String.format(
								"Got open quote %s[%s, %s, %s, %s, %s, %s]",
								quoteCandle.getSymbol(),
								quoteCandle.getOpenPrice(),
								quoteCandle.getHighPrice(),
								quoteCandle.getLowPrice(),
								quoteCandle.getClosePrice(),
								quoteCandle.getVolume(), quoteCandle.getDate()));

					}
				});

		quote.setLoopForever(true);
		quote.setInterval(1);
		quote.initialize();

	}

}
