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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openquant.backtest.Candle;

public abstract class QuoteDataSource {
	
	private Log log = LogFactory.getLog(QuoteDataSource.class);

	private Set<String> symbols = new HashSet<String>();

	private boolean loopForever = true;

	private int interval = 5;

	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(20);

	private ScheduledFuture<?> scheduledFuture;
	
	public interface GlobalQuoteListener{
		void onOpenQuote(Candle quote);
		void onQuote(Candle quote);
	}

	protected GlobalQuoteListener globalListener;
	
	public GlobalQuoteListener getGlobalListener() {
		return globalListener;
	}

	public void setGlobalListener(GlobalQuoteListener globalListener) {
		this.globalListener = globalListener;
	}
	
	public QuoteDataSource(Set<String> symbols, GlobalQuoteListener globalListener){
		this.symbols = symbols;
		this.globalListener = globalListener;
	}
	
	public QuoteDataSource(){
		
	}

	private class QuoteRetriever implements Runnable {

		@Override
		public void run() {
			
				Set<String> localSymbols = new HashSet<String>(getSymbols());
				
				log.debug(">> BEGIN retrieve");
				for (String symbol : localSymbols) {
					try {
						Candle candle = retrieveQuoteCandle(symbol);
						if (candle != null){
							onGetQuote( candle );
						}
					} catch (Exception e) {
						log.error("Exception when retrieving [" + symbol + "]", e);
					}
				}
				log.debug(">> END retrieve");
				
			
		}
	}
	
	private void retrieveOpenQuotes(){

		Set<String> localSymbols = new HashSet<String>(getSymbols());
		
		log.debug(">> BEGIN retrieve");
		for (String symbol : localSymbols) {
			try {
				Candle candle = retrieveQuoteCandle(symbol);
				if (candle != null){
					onGetOpenQuote( candle );
				}
			} catch (Exception e) {
				log.error("Exception when retrieving [" + symbol + "]", e);
			}
		}
		log.debug(">> END retrieve");
	}

	public boolean isLoopForever() {
		return loopForever;
	}

	public void setLoopForever(boolean loopForever) {
		this.loopForever = loopForever;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
		// reinitialize the schedule
		if (scheduledFuture != null){
			scheduledFuture.cancel(true);
			initialize();
		}
	}

	public Set<String> getSymbols() {
		return symbols;
	}

	public void setSymbols(Set<String> symbols) {
		this.symbols = symbols;
	}
	
	public void removeSymbol(String symbol){
		symbols.remove(symbol);
	}

	public void initialize() {
		
		// first get Open Quotes
		retrieveOpenQuotes();		
			
		if (isLoopForever()) {			
			scheduledFuture = executor.scheduleWithFixedDelay(new QuoteRetriever(), 0, getInterval(), TimeUnit.SECONDS);

		} else {
			scheduledFuture = executor.schedule(new QuoteRetriever(), getInterval(), TimeUnit.SECONDS);
		}
	}

	public void finalize() {
		scheduledFuture.cancel(false);
		
	}


	private void onGetQuote(final Candle quoteCandle) {

		if(log.isDebugEnabled()){
			log.debug(String.format("Got quote %s[%s, %s, %s, %s, %s, %s]", quoteCandle.getSymbol(),
					quoteCandle.getClosePrice(), quoteCandle.getOpenPrice(), quoteCandle.getHighPrice(),
					quoteCandle.getLowPrice(), quoteCandle.getVolume(), quoteCandle.getDate()));
		}
		
		this.globalListener.onQuote(quoteCandle);
		
	}
	
	private void onGetOpenQuote(final Candle quoteCandle) {

		if(log.isDebugEnabled()){
			log.debug(String.format("Got quote %s[%s, %s, %s, %s, %s, %s]", quoteCandle.getSymbol(),
					quoteCandle.getClosePrice(), quoteCandle.getOpenPrice(), quoteCandle.getHighPrice(),
					quoteCandle.getLowPrice(), quoteCandle.getVolume(), quoteCandle.getDate()));
		}
		
		this.globalListener.onOpenQuote(quoteCandle);
		
	}
	

	public abstract Candle retrieveQuoteCandle(String symbol) throws Exception;

}
