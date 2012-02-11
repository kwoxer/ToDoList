package de.fhb.maus.android.todolist.database;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;

import org.apache.commons.net.ftp.*;
import android.os.Environment;

public class IO {
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
	
	
	boolean ftpTransfer(String localfile, String destinationfile)
	{
		String server = "ftp.domain.com";
		String username = "ftpuser";
		String password = "ftppass";
		try
		{
			FTPClient ftp = new FTPClient();
			ftp.connect(server);
			if(!ftp.login(username, password))
			{
				ftp.logout();
				return false;
			}
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply))
			{
				ftp.disconnect();
				return false;
			}
			InputStream in = new FileInputStream(localfile);
			ftp.setFileType(ftp.BINARY_FILE_TYPE);
			boolean Store = ftp.storeFile(destinationfile, in);
			in.close();
			ftp.logout();
			ftp.disconnect();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		return true;
		
//		try
//		{
//		int x;
//		url = new URL("http://1.2.2.4");
//		HttpURLConnection con;
//		BufferedInputStream bufIn;
//		String user="admin";
//		String password="admin123";
//
//		String stringUserPassword = user + ":" + password;
//		String base64UserPassword = encode(stringUserPassword);
//
//		con = (HttpURLConnection)url.openConnection();
//
//		con.setDoOutput(true);
//		con.setDoInput(true);
//
//		con.setRequestProperty("Connection", "Keep-Alive");
//		con.setRequestProperty("Authorization", "Basic "+base64UserPassword);
//
//		con.connect();
//		}
//		catch (Exception e)
//		{
//		System.out.println("in catch" +e);
//		}
//		}
//		}
//		
	}

//	
//	boolean ftpTransfer(String localfile, String destinationfile)
//	{
//		String server = "ftp.domain.com";
//		String username = "ftpuser";
//		String password = "ftppass";
//		try
//		{
//			FTPClient ftp = new FTPClient();
//			ftp.connect(server);
//			if(!ftp.login(username, password))
//			{
//				ftp.logout();
//				return false;
//			}
//			int reply = ftp.getReplyCode();
//			if (!FTPReply.isPositiveCompletion(reply))
//			{
//				ftp.disconnect();
//				return false;
//			}
//			InputStream in = new FileInputStream(localfile);
//			ftp.setFileType(ftp.BINARY_FILE_TYPE);
//			boolean Store = ftp.storeFile(destinationfile, in);
//			in.close();
//			ftp.logout();
//			ftp.disconnect();
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//			return false;
//		}
//		return true;
//	}
}
