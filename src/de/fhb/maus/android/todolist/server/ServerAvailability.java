package de.fhb.maus.android.todolist.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerAvailability {

	public static boolean isReachable(String ip) {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL("http://" + ip)
					.openConnection();
			connection.setRequestMethod("HEAD");
			connection.setConnectTimeout(1000);
			int responseCode = connection.getResponseCode();
			
			if (responseCode != 200) {
				return false;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
}
