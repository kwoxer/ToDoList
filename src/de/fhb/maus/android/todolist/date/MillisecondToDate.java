package de.fhb.maus.android.todolist.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MillisecondToDate {

	public static String getDate(String milliSec) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(milliSec);
		return sdf.format(resultdate);
	}
}
