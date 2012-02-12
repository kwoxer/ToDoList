package de.fhb.maus.android.todolist.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import android.os.Environment;

public class IO {

	// http://stackoverflow.com/questions/7300044/trying-to-export-db-to-sdcard
	public static String getInternalDBPath() {
		String packageName = "de.fhb.maus.android.todolist";
		String dbName = "applicationdata";
		File dbPath = new File(Environment.getDataDirectory() + "/data/"
				+ packageName + "/databases/" + dbName);
		String currentDBPath = dbPath.getAbsolutePath();
		return currentDBPath;
	}
	public static String getExternalDBPath() {
		return "http://10.0.2.2/upload/database.db";
	}
	public static String getExternalUploadPHP() {
		return "http://10.0.2.2/upload/upload.php";
	}

	// http://stackoverflow.com/questions/2814213/making-a-database-backup-to-sdcard-on-android
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
					getExternalUploadPHP()).openConnection();
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

	public static void importDatabase() throws IllegalStateException,
			MalformedURLException, ProtocolException, IOException {

		String url_str = getExternalDBPath();
		FileOutputStream os = new FileOutputStream(getInternalDBPath());
		URL url = new URL(url_str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		int responseCode = conn.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			byte tmp_buffer[] = new byte[4096];
			InputStream is = conn.getInputStream();
			int n;
			while ((n = is.read(tmp_buffer)) > 0) {
				System.out.println(n);
				os.write(tmp_buffer, 0, n);
				os.flush();
			}
		}
		os.close();
	}
}
