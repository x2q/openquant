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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openquant.data.SeriesDatasource;
import org.openquant.data.YahooSeriesDatasource;

public class GroovyTradeSystem extends CandleSeriesTestContext {

	private String scriptFile;

	public GroovyTradeSystem(String scriptFile) {
		super();
		if( !scriptFile.endsWith(".groovy") ){
			// automatically append the groovy ext
			scriptFile += ".groovy";
		}
		this.scriptFile = scriptFile;
	}

	@Override
	public void run() {
		
		ScriptEngineManager factory = new ScriptEngineManager();

		ScriptEngine engine = factory.getEngineByName("groovy");


		try {
			engine.put("context", this);
		
			engine.eval( readFully ( findScript("scripts") ));

		} catch (ScriptException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static String readFully(File file) throws IOException {
		return readFully(new FileReader(file));
	}

	public static String readFully(Reader reader) throws IOException {
		char[] arr = new char[8 * 1024]; // 8K at a time
		StringBuffer buf = new StringBuffer();
		int numChars;

		while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
			buf.append(arr, 0, numChars);
		}

		return buf.toString();
	}

	private File findScript(String... directories) {
		for (String directory : directories) {
			for (File pathname : new File(directory).listFiles()) {
				if (pathname.isFile() 
					&& pathname.toString().equals(directory + File.separator + scriptFile)) {
					return pathname;
				}
			}
		}
		return null;
	}

	public static void main(String... args) {
		// new ClassPathXmlApplicationContext("TestTradeSystem.xml");

		SeriesDatasource data = new YahooSeriesDatasource("2009-01-01");

		List<String> symbols = new ArrayList<String>();
		symbols.add("GOOG");

		BackTestExecutor executor = new BackTestExecutor(data, symbols,
				new GroovyTradeSystem("Tester.groovy"), "TesterReport", 100000, 9.99,
				0.2);
		executor.run();
	}

}
