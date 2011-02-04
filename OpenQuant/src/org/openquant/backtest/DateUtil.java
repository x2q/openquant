package org.openquant.backtest;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		
		
		Date date = calculateLookbackDate(5);
		
		System.out.println(date);

	}
	
	public static Date calculateLookbackDate(int lookback){
	
		// today
		Date dayCounter = new Date();
		
		int validDatesCounter = lookback;
		//for(int i = 0 ; i < lookback; i++){
		while(validDatesCounter > 0){
			
			// check for weekends and holiday list
			
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(dayCounter);

			if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
					&& !holidayList.contains(dayCounter)) {
				
				
				validDatesCounter--;
			
			}
			dayCounter.setDate(dayCounter.getDate() - 1);
				
			
		
		}		
		
		return dayCounter;
		
	}
}
