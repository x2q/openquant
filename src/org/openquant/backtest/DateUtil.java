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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

	static List<Date> holidayList = new ArrayList<Date>();

	// holiday list
	static {
		holidayList.add(new Date("11/23/2010"));
	}

	public static void main(String args[]) {

		/*
		Date day1 = new Date("08/17/2009"); // start date
		Date day2 = new Date("08/27/2009"); // end date
		Date dayCounter = day1;
		int counter = 1;

		while (dayCounter.before(day2)) {

			// check for weekends and holiday list
			if (dayCounter.getDay() != Calendar.SATURDAY
					&& dayCounter.getDay() != Calendar.SUNDAY
					&& !holidayList.contains(dayCounter)) {
				counter++; // count weekdays
			}

			dayCounter.setDate(dayCounter.getDate() + 1);
		}

		System.out.println("Working days = " + counter);
		
		*/
		
		
		Date date = calculateLookbackDate(1);
		
		System.out.println(date);

	}
	
	public static Date calculateLookbackDate(int lookback){
	
		// today
		Date dayCounter = new Date();
		
		int validDatesCounter = lookback;
		while(validDatesCounter > 0){
			
			// check for weekends and holiday list			
			dayCounter.setDate(dayCounter.getDate() - 1);
			
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(dayCounter);

			if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
				&& !holidayList.contains(dayCounter)) {				
				validDatesCounter--;
			
			}		
		}		
		
		return dayCounter;
		
	}
}
