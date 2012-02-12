package de.fhb.maus.android.todolist.helpers;

import java.io.File;

import android.os.Environment;

public class PATHs {
	private static String packageName = "/data/de.fhb.maus.android.todolist/databases/";

	// http://stackoverflow.com/questions/7300044/trying-to-export-db-to-sdcard
	public static String getInternalDatabasePath() {

		String dbName = "applicationdata";
		File dbPath = new File(Environment.getDataDirectory() + packageName
				+ dbName);
		String currentDBPath = dbPath.getAbsolutePath();
		return currentDBPath;
	}
	public static String getInternalTimestampPath() {
		
		String tsName = "timestamp";
		File dbPath = new File(Environment.getDataDirectory() + packageName
				+ tsName);
		String currentDBPath = dbPath.getAbsolutePath();
		return currentDBPath;
	}
}