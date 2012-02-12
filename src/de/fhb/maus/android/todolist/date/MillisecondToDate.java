package de.fhb.maus.android.todolist.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class MillisecondToDate {
	private static Calendar mCalendar;
	private static DateFormat mDateFormat;
	
	public static String getDate(long milliSec) {
		System.out.println(milliSec);
		
		mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		mCalendar.setTimeInMillis(milliSec);
		String test = mDateFormat.format(mCalendar.getTime().getTime());
		return test;
	}
}