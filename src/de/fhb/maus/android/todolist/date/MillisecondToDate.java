package de.fhb.maus.android.todolist.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class MillisecondToDate {
	private static Calendar mCalendar;
	private static DateFormat mDateFormat;

	public static String getDate(long milliSec) {
		mCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));
		mDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
		mCalendar.setTimeInMillis(milliSec);
		return mDateFormat.format(mCalendar.getTime().getTime());
	}
}