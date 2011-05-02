package org.openquant.data;

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

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openquant.backtest.CandleSeries;

public class SeriesCollection implements Serializable {

	private static final long serialVersionUID = 1L;

	private Set<CandleSeries> seriesSet = new HashSet<CandleSeries>();

	public void addSeries(final CandleSeries series) {
		seriesSet.add(series);
	}
	
	public Set<String> getSymbols(){
		Set<String> rSet = new HashSet<String>();
		for (CandleSeries series : seriesSet){
			
			rSet.add(series.getSymbol());
		}
		return rSet;
	}
	
	public Date getBeginDate(){
		return seriesSet.iterator().next().getBeginDate();
	}
	
	public Date getEndDate(){
		return seriesSet.iterator().next().getEndDate();
	}
	
	public CandleSeries getSeries(String symbol){
		
		for (CandleSeries series : seriesSet){
			if (series.getSymbol().equals(symbol)){
				return series;
			}
		}
		return null;
		
	}

}
