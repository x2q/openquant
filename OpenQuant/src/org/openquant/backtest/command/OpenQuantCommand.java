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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.openquant.backtest.BackTestExecutor;
import org.openquant.backtest.GroovyTradeSystem;
import org.openquant.data.YahooSeriesDatasource;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.ShellFactory;

public class OpenQuantCommand {

	private double capital = 100000;

	private double commission = 9.99;

	private double slippage = 0.002;

	private static Map<String, SymbolSetSeries> symbolMap = new HashMap<String, SymbolSetSeries>();

	static {
		// load up all persisted lists
		for (File pathname : new File("data").listFiles()) {
			if (pathname.isFile()) {
				StringBuffer output = new StringBuffer(pathname.getName());
				// remove the extension
				output.replace(output.length() - 4, output.length(), "");
				String name = output.toString();
				symbolMap.put(name,
						new SymbolSetSeries(name, pathname.getName()));
			}
		}
	}

	@Command(abbrev = "cap", description = "Get starting capital")
	public double getCapital() {
		return capital;
	}

	@Command(abbrev = "scap", description = "Set starting capital")
	public void setCapital(double capital) {
		this.capital = capital;
	}

	@Command(abbrev = "slip", description = "Get percent of 1% slippage per trade")
	public double getSlippage() {
		return slippage;
	}

	@Command(abbrev = "sslip", description = "Set percent of 1% slippage per trade")
	public void setSlippage(double slippage) {
		this.slippage = slippage;
	}

	@Command(abbrev = "com", description = "Get commission per trade")
	public double getCommission() {
		return commission;
	}

	@Command(abbrev = "scom", description = "Set commission per trade")
	public void setCommission(
			@Param(name = "amount", description = "Amount of commission per trade") double amount) {
		this.commission = amount;
	}

	@Command(abbrev = "clist", description = "Creates a new stock list. ie. clist mylist 2005-01-01 YHOO GOOG MSFT")
	public void createStockList(
			@Param(name = "name", description = "The stock list's name") String name,
			@Param(name = "startDate [" + YahooSeriesDatasource.DATE_FORMAT
					+ "]", description = "The lookback start date as "
					+ YahooSeriesDatasource.DATE_FORMAT) String startDate,
			@Param(name = "stocks", description = "Spaced-separated list of stock symbols") String... stocks) {
		symbolMap.put(name, new SymbolSetSeries(name, new HashSet<String>(
				Arrays.asList(stocks)), startDate));
	}

	@Command(abbrev = "alist", description = "Add stocks to existing list")
	public void addToStockList(
			@Param(name = "name", description = "The stock list's name") String name,
			@Param(name = "stocks", description = "Spaced-separated list of stock symbols") String... stocks) {

		if (name == null) {
			System.out.println("Stocklist Name must be specified.");
		}
		symbolMap.get(name).getSymbols().addAll(Arrays.asList(stocks));

	}

	@Command(abbrev = "dlist", description = "Delete a specified stock list")
	public void deleteStockList(
			@Param(name = "name", description = "The stock list's name") String name) {
		SymbolSetSeries symbolSet = symbolMap.remove(name);
		symbolSet.delete();
	}

	@Command(abbrev = "ls", description = "List all stock lists")
	public void listAll() {

		for (String key : symbolMap.keySet()) {
			System.out.println(key + "\n");
		}
	}

	@Command(abbrev = "glist", description = "Prints out a specified stock list")
	public String getStockList(
			@Param(name = "name", description = "The stock list's name") String name) {
		StringBuffer output = new StringBuffer();

		if (name == null) {
			return output.append("Stocklist Name must be specified.")
					.toString();
		}

		SymbolSetSeries symbolSet = symbolMap.get(name);
		if (symbolSet == null) {
			return output.append(
					String.format("Symbol list [%s] not found.", name))
					.toString();
		}

		output.append(symbolSet);

		return output.toString();
	}

	@Command(abbrev = "test", description = "Backtest a strategy against a stock list")
	public void backTest(
			@Param(name = "stockList", description = "The stock list's name") String stockList,
			@Param(name = "tradeSystem", description = "The tradesystem script to test against") String tradeSystemScript) {

		SymbolSetSeries symbolSet = symbolMap.get(stockList);
		if (symbolSet == null) {
			System.out.println(String.format("Symbol list [%s] not found.",
					stockList));
			return;
		}

		BackTestExecutor executor = new BackTestExecutor(symbolSet,
				new ArrayList<String>(symbolSet.getSymbols()),
				new GroovyTradeSystem(tradeSystemScript), tradeSystemScript,
				capital, commission, slippage);

		double cap = executor.run();
		System.out.println(String.format("Ending capital is $ %10.2f", cap));
	}

	public static void main(String[] args) throws IOException {
		ShellFactory
				.createConsoleShell(
						"openquant",
						"Welcome to the OpenQuant 1.0 command console.\nType ?list for a list of commands.",
						new OpenQuantCommand()).commandLoop();
	}

}
