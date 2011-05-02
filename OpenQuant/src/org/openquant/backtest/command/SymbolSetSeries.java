package org.openquant.backtest.command;

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

import java.io.File;
import java.util.Set;

import org.openquant.backtest.CandleSeries;
import org.openquant.data.ObjectStreamSeriesDatasource;
import org.openquant.data.SeriesDatasource;
import org.openquant.data.YahooSeriesDatasource;

class SymbolSetSeries implements SeriesDatasource{	
	
	private String name;
	
	private YahooSeriesDatasource datasource;
	
	private ObjectStreamSeriesDatasource localSeriesDatasource;
			
	private Set<String> symbols;
	
	private String beginDate;
	
	private String endDate;

	public SymbolSetSeries(String name, Set<String> symbols, String beginDate) {
		super();
		this.name = name;
		this.symbols = symbols;
		this.beginDate = beginDate;
		this.setDatasource(new YahooSeriesDatasource(beginDate));		
		initialize();
	}
	
	public SymbolSetSeries(String name, Set<String> symbols, String beginDate, String endDate) {
		super();
		this.name = name;
		this.symbols = symbols;
		this.beginDate = beginDate;
		this.setDatasource(new YahooSeriesDatasource(beginDate, endDate));
		initialize();
	}
	
	public SymbolSetSeries(String name, String file){
		super();
		this.name = name;
		this.localSeriesDatasource = new ObjectStreamSeriesDatasource( String.format("data/%s.dat", name));
		this.symbols = localSeriesDatasource.getSymbols();
		this.beginDate = localSeriesDatasource.getBeginDate().toString();
		this.endDate = localSeriesDatasource.getEndDate().toString();
	}
	
	public void delete(){
		new File(String.format("data/%s.dat", name)).delete();
	}
	
	private void initialize(){
				
		this.localSeriesDatasource = new ObjectStreamSeriesDatasource( String.format("data/%s.dat", name));
			
		if(!this.localSeriesDatasource.exists()){
			try{
			
				for (String symbol : symbols){
					try {
						CandleSeries series = datasource.fetchSeries(symbol);
						localSeriesDatasource.insertCandleSeries(series);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			}finally{
				localSeriesDatasource.commit();
			}
		}
			
		
	}
	
	public Set<String> getSymbols() {
		return symbols;
	}

	public void setSymbols(Set<String> symbols) {
		this.symbols = symbols;
	}

	@Override
	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append(String.format("[%s, %s]",name, beginDate));
		output.append("\n");
		
		for (String symbol : symbols){
			output.append(symbol + "\n");
		}
		
		// remove the last newline
		output.replace(output.length()-1, output.length(), "");
		
		return output.toString();
	}

	public void setDatasource(YahooSeriesDatasource datasource) {
		this.datasource = datasource;
	}

	public YahooSeriesDatasource getDatasource() {
		return datasource;
	}

	@Override
	public CandleSeries fetchSeries(String symbol) throws Exception {
		
		return localSeriesDatasource.fetchSeries(symbol);
		
	}
	
}