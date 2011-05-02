package org.openquant.data.load;

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

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.CandleSeries;
import org.openquant.data.ObjectStreamSeriesDatasource;
import org.openquant.data.YahooSeriesDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Utility class for importing data from yahoo into a local
 * ObjectStreamSeriesDatasource. During backtesting the
 * ObjectStreamSeriesDatasource can replace the usual YahooSeriesDatasource so
 * the test does not have to go out and fetch the data from the Yahoo's servers
 * for each stock.  
 * 
 * This example will autowire the contents of symbols-yahoo-nasdaq10.xml into 
 * the symbols variable and export the resulting Collection to the file specified
 * via the DATAFILE static var at a specific START_DATE to todays closing date.
 * 
 * @author jay
 * 
 */
@Configuration
@ImportResource("classpath:/symbols-yahoo-nasdaq10.xml")
public class DataLoader {

	private static Log log = LogFactory.getLog(DataLoader.class);

	private static final String START_DATE = "2000-01-01";

	private static final String DATAFILE = "nasdaq10.dat";

	@Autowired
	@Resource(name = "symbols")
	private Set<String> symbols;

	private ObjectStreamSeriesDatasource localSeriesDatasource = new ObjectStreamSeriesDatasource(DATAFILE);

	public class ExecuteBean {

		public void loadFromYahoo() {

			try {

				for (String symbol : symbols) {
					try {
						CandleSeries series = yahooSeriesDatasource().fetchSeries(symbol);
						localSeriesDatasource.insertCandleSeries(series);
						log.info("Loaded series " + symbol);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}

			} finally {
				localSeriesDatasource.commit();
			}

		}
	}

	@Bean
	public YahooSeriesDatasource yahooSeriesDatasource() {
		return new YahooSeriesDatasource(START_DATE);
	}

	@Bean(name = "executeBean", initMethod = "loadFromYahoo")
	public ExecuteBean executeBean() {
		return new ExecuteBean();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(DataLoader.class);
	}

}
