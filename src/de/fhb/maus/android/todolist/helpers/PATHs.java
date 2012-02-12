package de.fhb.maus.android.todolist.helpers;

import java.io.File;

import android.os.Environment;

public class PATHs {

	/**
	 * building the path to the database data on the device
	 * @return path of database
	 */
	// http://stackoverflow.com/questions/7300044/trying-to-export-db-to-sdcard
	public static String getInternalDatabasePath() {

		String dbName = "applicationdata";
		File dbPath = new File(Environment.getDataDirectory() + URLs.getPackageName()
				+ dbName);
		String currentDBPath = dbPath.getAbsolutePath();
		return currentDBPath;
	}
	
	/**
	 * building the path to the timestamp data on the device
	 * @return path of timestamp
	 */
	public static String getInternalTimestampPath() {
		
		String tsName = "timestamp";
		File dbPath = new File(Environment.getDataDirectory() + URLs.getPackageName()
				+ tsName);
		String currentDBPath = dbPath.getAbsolutePath();
		return currentDBPath;
	}
}