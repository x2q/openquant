package org.openquant.data;

import org.openquant.backtest.CandleSeries;

public interface SeriesDatasource {

	public abstract CandleSeries fetchSeries(final String symbol) throws Exception;

}