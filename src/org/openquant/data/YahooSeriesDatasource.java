package org.openquant.data;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Candle;
import org.openquant.backtest.CandleSeries;
import org.openquant.backtest.DateUtil;

public class YahooSeriesDatasource implements SeriesDatasource {

	private Log log = LogFactory.getLog(YahooSeriesDatasource.class);

	private final static String YAHOO_URL = "http://table.finance.yahoo.com/table.csv?s={0}&a={1}&b={2}&c={3}&d={4}&e={5}&f={6}&g={7}&ignore=.csv";

	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	private final static DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

	private boolean splitAdjust = true;
	
	private Date begin;

	private Date end;
	
	
	public YahooSeriesDatasource(String begin, String end) {

		try {
			this.begin = sdf.parse(begin);
			this.end = sdf.parse(end);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
	}

	public YahooSeriesDatasource(String begin) {

		try {
			this.begin = sdf.parse(begin);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public YahooSeriesDatasource(final int lookback){
		this.begin = DateUtil.calculateLookbackDate(lookback);
	}
	
	public boolean isSplitAdjust() {
		return splitAdjust;
	}

	public void setSplitAdjust(boolean splitAdjust) {
		this.splitAdjust = splitAdjust;
	}
	
	public void setBeginDate(String beginDate){
		try {
			this.begin = sdf.parse(beginDate);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void setEndDate(String endDate){
		try {
			this.end = sdf.parse(endDate);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.openquant.backtest.SeriesDatasource#fetchSeries(java.lang.String)
	 */
	public CandleSeries fetchSeries(final String symbol) throws Exception {

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(begin);
		String beginYear = String.valueOf(cal.get(Calendar.YEAR));
		String beginMonth = String.valueOf(cal.get(Calendar.MONTH));
		String beginDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

		// if end not set, assume yesterday's date
		if (end == null) {
			// get Calendar with current date
			GregorianCalendar gCal = new GregorianCalendar();

			// get yesterday's date
			gCal.add(Calendar.DATE, -1);

			end = gCal.getTime();
		}

		cal.setTime(end);
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		String month = String.valueOf(cal.get(Calendar.MONTH));
		String year = String.valueOf(cal.get(Calendar.YEAR));

		String resolution = "d";

		// open url
		String urlStr = MessageFormat.format(YAHOO_URL, symbol, beginMonth, beginDay, beginYear, month, day, year,
				resolution);
		BufferedReader reader;
		String line;

		List<String> lineList = new ArrayList<String>();

		log.info("URL [" + urlStr + "]");
		URL url = new URL(urlStr);
		reader = new BufferedReader(new InputStreamReader(url.openStream()));
		line = reader.readLine();
		log.debug(line);

		while ((line = reader.readLine()) != null) {
			lineList.add(0, line);
		}

		List<Candle> candles = new ArrayList<Candle>();

		for (String currentLine : lineList) {
			log.debug(currentLine);
			StringTokenizer str = new StringTokenizer(currentLine, ",");
			String datestring = str.nextToken();

			double open = round(Double.parseDouble(str.nextToken()), 2);
			double high = Double.parseDouble(str.nextToken());
			double low = Double.parseDouble(str.nextToken());
			double close = Double.parseDouble(str.nextToken());
			Long volume = null;
			double adjclose = 0;
			if (str.hasMoreTokens()) {
				String volString = str.nextToken();
				volume = Long.parseLong(volString);
				log.trace( String.format("Parsed volume [%s] to %s", volString, volume));

				if (splitAdjust) {
					adjclose = Double.parseDouble(str.nextToken());
				}
			}

			Date date = sdf.parse(datestring);

			Candle candle = null;
			if (splitAdjust) {
				double adjustmentFactor = adjclose / close;

				candle = new Candle(symbol, date, round(open * adjustmentFactor, 2), round(high * adjustmentFactor, 2),
						round(low * adjustmentFactor, 2), adjclose, volume);
			} else {
				// new candle
				candle = new Candle(symbol, date, open, high, low, close, volume);
			}

			candles.add(candle);

		}

		return new CandleSeries(candles);
	}

	public static double round(double Rval, int Rpl) {
		double p = (double) Math.pow(10, Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (double) tmp / p;
	}

}
