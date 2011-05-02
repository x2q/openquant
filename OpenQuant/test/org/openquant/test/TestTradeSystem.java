package org.openquant.test;

import java.util.ArrayList;
import java.util.List;

import org.openquant.backtest.BackTestExecutor;
import org.openquant.backtest.CandleSeriesTestContext;
import org.openquant.backtest.Position;
import org.openquant.backtest.Series;
import org.openquant.data.SeriesDatasource;
import org.openquant.data.YahooSeriesDatasource;

public class TestTradeSystem extends CandleSeriesTestContext{

	@Override
	public void run() {
		
		Series Close = closeSeries();
		Series RSI = Close.RSI(25);

		for (int bar = 25; bar < barsCount() - 1; bar++) {
			
			Position pos = getLastOpenPosition();
			if (hasOpenPositions()) {
				sellAtLimit(bar + 1, pos, pos.getEntryPrice() * 1.02, "XLL");
			}

			if (hasOpenPositions()) {
				sellAtStop(bar + 1, pos, pos.getEntryPrice() * 0.99, "XLS");
			}
			
			if (RSI.getAt(bar) > RSI.getAt(bar - 1)) {
				buyAtLimit(bar + 1, close(bar), 1000, "ELL");
			}
			
			
		}

	}
	
	public static void main(String... args) {
		//new ClassPathXmlApplicationContext("TestTradeSystem.xml");

		SeriesDatasource data = new YahooSeriesDatasource("2009-01-01");

		List<String> symbols = new ArrayList<String>();
		symbols.add("GOOG");

		BackTestExecutor executor = new BackTestExecutor(data, symbols, new TestTradeSystem(), "TestReport", 100000, 9.99, 0.2);
		executor.run();
	}

}
