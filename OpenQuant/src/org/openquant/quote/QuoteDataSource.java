package org.openquant.quote;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

	private ScheduledFuture scheduledFuture;

	protected GlobalQuoteListener globalListener;
	
	public GlobalQuoteListener getGlobalListener() {
		return globalListener;
	}

	public void setGlobalListener(GlobalQuoteListener globalListener) {
		this.globalListener = globalListener;
	}

	public interface GlobalQuoteListener{
		void onGetQuote(Candle quote);
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
			
				//List<String> localSymbols = Arrays.asList(new String[getSymbols().size()]);
				Set<String> localSymbols = new HashSet<String>(getSymbols());
				//Collections.copy(localSymbols,getSymbols());
				
				log.info(">> BEGIN retreive");
				for (String symbol : localSymbols) {
					try {
						Candle candle = retrieveQuoteCandle(symbol);
						if (candle != null){
							onGetQuote( candle );
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				log.info(">> END retreive");
				
			
		}
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
		
		this.globalListener.onGetQuote(quoteCandle);
		
	}

	public abstract Candle retrieveQuoteCandle(String symbol);

}
