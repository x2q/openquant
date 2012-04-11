package org.openquant.util;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * source : http://www.isthemarketopen.com/
 * 
 * @author jay
 *
 */
public class OpenMarketUtil {
	
	private static List<Day> closed = new ArrayList<Day>();
	
	static{
		closed.add(new Day(2011, Calendar.JANUARY, 17));
		closed.add(new Day(2011, Calendar.FEBRUARY, 21));
		closed.add(new Day(2011, Calendar.APRIL, 22));
		closed.add(new Day(2011, Calendar.MAY, 30));
		closed.add(new Day(2011, Calendar.JULY, 4));
		closed.add(new Day(2011, Calendar.SEPTEMBER, 5));
		closed.add(new Day(2011, Calendar.NOVEMBER, 24));
		closed.add(new Day(2011, Calendar.DECEMBER, 26));
	}
	
	public static boolean isMarketOpenToday(){
		return isMarketOpen(new Day(new Date()));
	}
	
	public static boolean isMarketOpen(Day day){

		int position = Collections.binarySearch(closed, day);
				
		return position < 0;
		
	}
	
	public static void main(String ... args){
		if (OpenMarketUtil.isMarketOpenToday()){
			System.out.println("Market is open");
		}else{
			System.out.println("Market is closed");
		}
	}

}
