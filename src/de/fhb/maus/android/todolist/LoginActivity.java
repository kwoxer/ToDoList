package de.fhb.maus.android.todolist;

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
import de.fhb.maus.android.todolist.database.IO;
import de.fhb.maus.android.todolist.server.ServerAvailability;
import de.fhb.maus.android.todolist.validator.EmailValidator;

public class LoginActivity extends Activity {
	
	private Button mLogIn, mExit;
	private boolean toastAlreadyShown = false;
	private EditText mEmailField, mPwField;
	private TextView mError, mServer;
	private EmailValidator mEv;
	private String phpAddress = "http://10.0.2.2/login/login.php",
			serverAddress = "10.0.2.2";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mEmailField = (EditText) findViewById(R.id.editTextEmail);
		mPwField = (EditText) findViewById(R.id.editTextPassword);
		mLogIn = (Button) findViewById(R.id.buttonLogin);
		mLogIn.setEnabled(false);
		mError = (TextView) findViewById(R.id.textViewError);
		mServer = (TextView) findViewById(R.id.textViewServerAvailability);
		mExit = (Button) findViewById(R.id.buttonExit);

		IO.exportDB();
		
		if (ServerAvailability.isReachable(serverAddress))
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

		mEmailField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String email = mEmailField.getText().toString();
					Log.v("email=", "");
					mEv = new EmailValidator();
					if (!email.isEmpty() && mEv.validate(email)) {
						mLogIn.setEnabled(true);
					} else {
						mLogIn.setEnabled(false);
					}
					return true;
				}
				return false;
			}
		});
		
		mEmailField.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mError.setText("");
			}
		});

		mPwField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String pw = mPwField.getText().toString();
					if (pw.length() == 6) {
						return true;
					} else {
						mPwField.setText("");
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

		mPwField.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mError.setText("");
				}
				return false;
			}
		});

		mEmailField.setOnTouchListener(new OnTouchListener() {
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
				String username = mEmailField.getText().toString();
				String password = mPwField.getText().toString();
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