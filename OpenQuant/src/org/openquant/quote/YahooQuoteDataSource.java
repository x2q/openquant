package org.openquant.quote;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Candle;

public class YahooQuoteDataSource extends QuoteDataSource {

	private Log log = LogFactory.getLog(YahooQuoteDataSource.class);

	private static final String QUOTE_URL = "http://quote.yahoo.com/d/quotes.csv?s=%s&d=t&f=sl1d1t1c1ohgvj1pp2wern";

	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yyyy hh:mmaaa");

	public YahooQuoteDataSource(Set<String> symbols, GlobalQuoteListener globalListener) {
		super(symbols, globalListener);
	}

	public YahooQuoteDataSource(Set<String> symbols) {
		this.setSymbols(symbols);
	}

	@Override
	public Candle retrieveQuoteCandle(String symbol) throws Exception {
		String urlStr = String.format(QUOTE_URL, symbol);
		String line = readLine(urlStr);

		Candle returnCandle = null;
		// "MSFT",28.46,"5/7/2010","1:23pm",-0.52,28.90,28.94,27.32,94535576,249.4B,28.98,"-1.79%","19.01 - 31.58",1.93,15.02,"Microsoft Corpora"

		StringTokenizer str = new StringTokenizer(line, ",\"");
		// symbol is the next token
		str.nextToken();
		double quote = Double.parseDouble(str.nextToken());
		Date date;
		try {
			date = DATE_FORMAT.parse(str.nextToken() + " " + str.nextToken());
		} catch (Exception e) {
			log.warn("Exception when parsing date for " + symbol + " using system timestamp");
			date = new Date();
		}
		// price change is the next token
		str.nextToken();
		double open = Double.parseDouble(str.nextToken());
		double high = Double.parseDouble(str.nextToken());
		double low = Double.parseDouble(str.nextToken());
		double volume = Double.parseDouble(str.nextToken());

		returnCandle = new Candle(symbol, date, open, high, low, quote, volume);

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

		Set<String> symbols = new HashSet<String>();
		symbols.add("MSFT");
		symbols.add("GOOG");

		QuoteDataSource quote = new YahooQuoteDataSource(symbols, new GlobalQuoteListener() {

			@Override
			public void onQuote(Candle quoteCandle) {
				log.info(String.format("Got quote %s[%s, %s, %s, %s, %s, %s]", quoteCandle.getSymbol(),
						quoteCandle.getOpenPrice(), quoteCandle.getHighPrice(), quoteCandle.getLowPrice(),
						quoteCandle.getClosePrice(), quoteCandle.getVolume(), quoteCandle.getDate()));

			}

			@Override
			public void onOpenQuote(Candle quoteCandle) {
				log.info(String.format("Got open quote %s[%s, %s, %s, %s, %s, %s]", quoteCandle.getSymbol(),
						quoteCandle.getOpenPrice(), quoteCandle.getHighPrice(), quoteCandle.getLowPrice(),
						quoteCandle.getClosePrice(), quoteCandle.getVolume(), quoteCandle.getDate()));
				
			}
		});

		quote.setLoopForever(true);
		quote.setInterval(1);
		quote.initialize();

	}

}
