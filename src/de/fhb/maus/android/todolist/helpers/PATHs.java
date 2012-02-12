package de.fhb.maus.android.todolist.helpers;

import java.io.File;

import android.os.Environment;

public class PATHs {
	// http://stackoverflow.com/questions/7300044/trying-to-export-db-to-sdcard
	public static String getInternalDBPath() {
		String packageName = "de.fhb.maus.android.todolist";
		String dbName = "applicationdata";
		File dbPath = new File(Environment.getDataDirectory() + "/data/"
				+ packageName + "/databases/" + dbName);
		String currentDBPath = dbPath.getAbsolutePath();
		return currentDBPath;
	}
}
