package de.fhb.maus.android.todolist.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import android.os.Environment;

public class IO {

	public static String getDBName() {
		String packageName = "de.fhb.maus.android.todolist";
		String dbName = "applicationdata";
		File dbPath = new File(Environment.getDataDirectory() + "/data/"
				+ packageName + "/databases/" + dbName);
		String currentDBPath = dbPath.getAbsolutePath();
		return currentDBPath;
	}

	public static void exportDatabase(String localFileName) {
		HttpURLConnection httpUrlConnection = null;
		OutputStream outputStream;
		BufferedInputStream fileInputStream;
		BufferedReader serverReader;
		int totalBytes;
		int bytesTrasferred;
		String response = "";
		String serverResponse = "";

		// Establish a connection
		try {
			httpUrlConnection = (HttpURLConnection) new URL(
					"http://10.0.2.2/upload/upload.php").openConnection();
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setRequestMethod("POST");
			outputStream = httpUrlConnection.getOutputStream();

			// Buffered input stream
			fileInputStream = new BufferedInputStream(new FileInputStream(
					localFileName));

			// Get the size of the image
			totalBytes = fileInputStream.available();

			// Loop through the files data
			for (int i = 0; i < totalBytes; i++) {
				// Write the data to the output stream
				outputStream.write(fileInputStream.read());
				bytesTrasferred = i + 1;
			}

			// Close the output stream
			outputStream.close();

			// New reader to get server response
			serverReader = new BufferedReader(new InputStreamReader(
					httpUrlConnection.getInputStream()));

			// Read the servers response
			serverResponse = "";
			while ((response = serverReader.readLine()) != null) {
				serverResponse = serverResponse + response;
			}

			// Close the buffered reader
			serverReader.close();

			// Close the file input stream
			fileInputStream.close();
			System.out.println("Database successfully saved on HTTP Server");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void exportDB() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			System.out.println(sd);
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
