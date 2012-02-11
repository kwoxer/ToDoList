package de.fhb.maus.android.todolist.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerAvailability {
	public static boolean isReachable(String ip) {
		try {
			Process exec = Runtime.getRuntime().exec("ping " + ip);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					exec.getInputStream()));
			reader.readLine();// PING...bytes of data.
			String line1 = reader.readLine().trim();
			String line2 = reader.readLine().trim();
			exec.destroy();
			return line1.endsWith("ms") && line2.endsWith("ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
