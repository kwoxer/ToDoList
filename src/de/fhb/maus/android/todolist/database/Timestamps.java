package de.fhb.maus.android.todolist.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.fhb.maus.android.todolist.helpers.URLs;

public class Timestamps {
	
	public static void exportTimestamp(String localFileName) {
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
			System.out.println("Database successfully saved on HTTP Server");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
