package org.openquant.backtest.intraday;

import java.util.Date;

import org.openquant.backtest.Candle;

public class IntradayCandle extends Candle {

	private static final long serialVersionUID = 3611441350748138120L;
	
	public enum CANDLETYPE{
		TICK,
		OPEN,
		HIGH,
		LOW,
		CLOSE
	}
	
	private CANDLETYPE type = CANDLETYPE.TICK;	

	public IntradayCandle() {
		super();
	}

	public IntradayCandle(String symbol, Date date, double open, double high,
			double low, double close, double volume) {
		super(symbol, date, open, high, low, close, volume);
	}

	public void setType(CANDLETYPE type) {
		this.type = type;
	}

	public CANDLETYPE getType() {
		return type;
	}
	
}
