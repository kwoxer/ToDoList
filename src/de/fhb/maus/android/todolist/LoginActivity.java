package de.fhb.maus.android.todolist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.fhb.maus.android.todolist.database.CustomHttpClient;
import de.fhb.maus.android.todolist.validator.EmailValidator;

public class LoginActivity extends Activity {
	/** Called when the activity is first created. */

	private Button mLogIn, mExit;
	private boolean toastAlreadyShown = false;
	private EditText emailField, pwField;
	private TextView mError, mServer;
	private EmailValidator ev;
	private String phpAddress = "http://10.0.2.2/login/login.php",
			serverAddress = "10.0.2.2";

	private boolean isReachable(String ip) {
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		emailField = (EditText) findViewById(R.id.editTextEmail);
		pwField = (EditText) findViewById(R.id.editTextPassword);
		mLogIn = (Button) findViewById(R.id.buttonLogin);
		mLogIn.setEnabled(false);
		mError = (TextView) findViewById(R.id.textViewError);
		mServer = (TextView) findViewById(R.id.textViewServerAvailability);
		mExit = (Button) findViewById(R.id.buttonExit);

		if (isReachable(serverAddress))
			mServer.setText("Server available!");
		else
			mServer.setText("Server not available!");

		mExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this,
						TodoListActivity.class));
			}
		});

		emailField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String email = emailField.getText().toString();
					Log.v("email=", "");
					ev = new EmailValidator();
					if (!email.isEmpty() && ev.validate(email)) {
						mLogIn.setEnabled(true);
					} else {
						mLogIn.setEnabled(false);
					}
					return true;
				}
				return false;
			}
		});
		emailField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mError.setText("");
			}
		});

		pwField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String pw = pwField.getText().toString();
					if (pw.length() == 6) {
						return true;
					} else {
						pwField.setText("");
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.passwort_to_short),
								Toast.LENGTH_SHORT).show();
					}
					return true;
				}
				return false;
			}
		});

		pwField.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mError.setText("");
				}
				return false;
			}
		});

		emailField.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mError.setText("");
				}
				return false;
			}
		});

		mLogIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = emailField.getText().toString();
				String password = pwField.getText().toString();
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair("name", username));
				postParameters.add(new BasicNameValuePair("pw", password));
				String response = null;
				try {
					response = CustomHttpClient.executeHttpPost(phpAddress,
							postParameters);
					String res = response.toString();
					res = res.replaceAll("\\s+", "");
					if (res.equals("1")) {
						mError.setText("Login accepted");
						startActivity(new Intent(LoginActivity.this,
								TodoListActivity.class));
					} else {
						mError.setText("Login not accepted");
					}
				} catch (Exception e) {
					Log.e("Database", e.toString());
				}
				if (!toastAlreadyShown) {
					Toast toast = Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.login_toast),
							Toast.LENGTH_SHORT);
					toast.show();
					toastAlreadyShown = true;
				}
			}
		});
	}
}