package de.fhb.maus.android.todolist.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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
import de.fhb.maus.android.todolist.helpers.PATHs;
import de.fhb.maus.android.todolist.helpers.URLs;

public class IO {

	/**
	 * Used for saving the database on the HTTP Apache server
	 * @param localFileName
	 */
	// http://stackoverflow.com/questions/2814213/making-a-database-backup-to-sdcard-on-android
	public static void exportDatabase() {
		HttpURLConnection httpUrlConnection = null;
		OutputStream outputStream;
		BufferedInputStream fileInputStream;
		BufferedReader serverReader;
		int totalBytes;
		int bytesTrasferred;
		String response = "";
		String serverResponse = "";
		String localFileName=PATHs.getInternalDatabasePath();

		// Establish a connection
		try {
			httpUrlConnection = (HttpURLConnection) new URL(
					URLs.getExternalUploadPHP()).openConnection();
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
			System.out.println("Database successfully saved on HTTP server");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used for saving the database on the emulator
	 * @throws IllegalStateException
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static void importDatabase() throws IllegalStateException,
			MalformedURLException, ProtocolException, IOException {

		String url_str = URLs.getExternalDatabasePath();
		FileOutputStream os = new FileOutputStream(PATHs.getInternalDatabasePath());
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
				os.write(tmp_buffer, 0, n);
				os.flush();
			}
		}
		os.close();
		System.out.println("Database successfully saved on the device");
	}
}
