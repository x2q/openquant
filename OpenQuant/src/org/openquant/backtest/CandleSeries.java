package org.openquant.backtest;

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

import java.util.ArrayList;
import java.util.List;

public class CandleSeries {

	private List<Candle> candles = new ArrayList<Candle>();

	public String getSymbol() {
		return candles.get(0).getSymbol();
	}

	public CandleSeries(List<Candle> candles) {
		this.candles = candles;
	}

	public int size() {
		return candles.size();
	}

	public Candle get(int index) {
		return candles.get(index);
	}

	public Series getOpenSeries() {

		double[] rArray = new double[candles.size()];

		int i = 0;
		for (Candle candle : candles) {
			rArray[i++] = candle.getOpenPrice();
		}

		return new Series(rArray);
	}

	public Series getCloseSeries() {

		double[] rArray = new double[candles.size()];

		int i = 0;
		for (Candle candle : candles) {
			rArray[i++] = candle.getClosePrice();
		}

		return new Series(rArray);
	}

	public Series getHighSeries() {

		double[] rArray = new double[candles.size()];

		int i = 0;
		for (Candle candle : candles) {
			rArray[i++] = candle.getHighPrice();
		}

		return new Series(rArray);
	}

	public Series getLowSeries() {

		double[] rArray = new double[candles.size()];

		int i = 0;
		for (Candle candle : candles) {
			rArray[i++] = candle.getLowPrice();
		}

		return new Series(rArray);
	}

	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		for (Candle candle : candles) {
			buffer.append(candle.toString());
			buffer.append("\n");
		}

		return buffer.toString();
	}

}
