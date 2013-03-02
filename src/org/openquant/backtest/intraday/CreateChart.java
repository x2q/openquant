package org.openquant.backtest.intraday;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.date.DayOfWeekInMonthRule;
import org.openquant.backtest.Series;
import org.openquant.data.SeriesDatasource;

public class CreateChart extends AbstractIntradayTest {

	@Override
	public void run() {

		System.out.println("Beginning test...");

		createChart(createDataset());

	}

	private DefaultHighLowDataset createDataset() {

		int serice = getCandleSeries().getDayCount();

		Date[] date = new Date[serice];
		double[] high = new double[serice];
		double[] low = new double[serice];
		double[] open = new double[serice];
		double[] close = new double[serice];
		double[] volume = new double[serice];

		Series highs = dayHighSeries();
		Series lows = dayLowSeries();
		Series closes = dayCloseSeries();
		Series opens = dayOpenSeries();

		IntradayCandleSeries daySeries = getCandleSeries();

		for (int i = 0; i < getCandleSeries().getDayCount(); i++) {

			date[i] = daySeries.getDayCandles(i).get(0).getDate();
			high[i] = highs.getAt(i);
			low[i] = lows.getAt(i);
			open[i] = opens.getAt(i);
			close[i] = closes.getAt(i);
			volume[i] = 0;

		}

		DefaultHighLowDataset data = new DefaultHighLowDataset("", date, high, low, open, close, volume);
		return data;
	}

	private void createChart(final DefaultHighLowDataset dataset) {
		final JFreeChart chart = ChartFactory.createCandlestickChart("Candlestick Demo", "Time", "Price", dataset,
				false);

		try {
			ChartUtilities.saveChartAsJPEG(new File("MSFT-Candle"+".jpg"), chart, 1000, 600);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void debugCandles(List<IntradayCandle> cList) {

		for (IntradayCandle c : cList) {
			System.out.println(c);
		}

	}

	public static void main(String... args) {

		SeriesDatasource data = new IntradaySeriesDatasource("/home/jay/StockDatabase/Test");
		List<String> symbols = new ArrayList<String>();

		symbols.add("MSFT");

		IntradayBackTestExecutor executor = new IntradayBackTestExecutor(data, symbols, new CreateChart(), "ATest");

		executor.run();

	}
}
