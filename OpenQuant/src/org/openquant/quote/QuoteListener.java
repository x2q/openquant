package org.openquant.quote;

import org.openquant.backtest.Candle;

public abstract class QuoteListener{

	protected String symbol;
	
	public QuoteListener(){		
	}
	
	public QuoteListener(String symbol){	
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public abstract void onGetQuote(Candle quoteCandle);
}
