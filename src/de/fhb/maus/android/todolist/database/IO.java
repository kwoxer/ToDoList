package de.fhb.maus.android.todolist.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import android.os.Environment;

public class IO {
	public static void exportDB() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			if (sd.canWrite()) {
				String currentDBPath = "data/data/com.mypack.myapp/databases/mydb.db";
				String backupDBPath = sd + "/filename.db";
				File currentDB = new File(currentDBPath);
				File backupDB = new File(backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void importDB() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			if (sd.canWrite()) {
				String currentDBPath = sd + "/filename.db";
				String backupDBPath = "data/data/com.mypack.myapp/databases/mydb_2.db";
				File currentDB = new File(currentDBPath);
				File backupDB = new File(backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
