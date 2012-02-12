package de.fhb.maus.android.todolist.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerAvailability {
	public static boolean isReachable1(String ip) {
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

	public static boolean isReachable2(String ip) {
		try {
			InetAddress address = InetAddress.getByName(ip);
			System.out.println("Name: " + address.getHostName());
			System.out.println("Addr: " + address.getHostAddress());
			System.out.println("Reach: " + address.isReachable(3000));
		} catch (UnknownHostException e) {
			System.err.println("Unable to lookup web.mit.edu");
		} catch (IOException e) {
			System.err.println("Unable to reach web.mit.edu");
		}
		return true;
	}

	public static boolean isReachable3(String ip) throws UnknownHostException,
			IOException {
		Socket socket = null;
		boolean reachable = false;
		try {
			socket = new Socket(ip, 8082);
			reachable = true;
		} finally {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
				}
		}
		return reachable;
	}
	
	public static boolean isReachable4(String ip) {
		try {
			return  InetAddress.getByName(ip).isReachable(4000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
}
