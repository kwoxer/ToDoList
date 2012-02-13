package de.fhb.maus.android.todolist;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import de.fhb.maus.android.todolist.database.CustomHttpClient;
import de.fhb.maus.android.todolist.date.MillisecondToDate;
import de.fhb.maus.android.todolist.helpers.URLs;
import de.fhb.maus.android.todolist.io.IO;
import de.fhb.maus.android.todolist.server.ServerAvailability;
import de.fhb.maus.android.todolist.timestamp.Timestamps;
import de.fhb.maus.android.todolist.validator.EmailValidator;

public class LoginActivity extends Activity {

	private Button mLogIn, mLogInLocal;
	private EditText mEmailField, mPwField;
	private TextView mErrorText, mServerAvailabilityText, mServerTimestamp,
			mServerText, mDeviceTimestamp, mCheckSyncText;
	private EmailValidator mEv;
	private PopupWindow mPw;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mEmailField = (EditText) findViewById(R.id.editTextEmail);
		mPwField = (EditText) findViewById(R.id.editTextPassword);
		mLogIn = (Button) findViewById(R.id.buttonLogin);
		mLogIn.setEnabled(false);
		mErrorText = (TextView) findViewById(R.id.textViewError);
		mServerAvailabilityText = (TextView) findViewById(R.id.textViewServerAvailability);
		mServerText = (TextView) findViewById(R.id.textViewServerText);
		mServerTimestamp = (TextView) findViewById(R.id.textViewServerTimestamp);
		mDeviceTimestamp = (TextView) findViewById(R.id.textViewDeviceTimestamp);
		mCheckSyncText = (TextView) findViewById(R.id.textViewDifference);
		mLogInLocal = (Button) findViewById(R.id.buttonLoginLocal);

		// create the timestamp on the device just at first start
		Timestamps.createTimestampOnDevice();

		// set a watcher on Email and Password
		TextWatcher watcher = new LocalTextWatcher();
		mEmailField.addTextChangedListener(watcher);
		mPwField.addTextChangedListener(watcher);

		// check availability of server
		if (ServerAvailability.isReachable(URLs.getExternalServerIP())) {
			// if server available
			mServerAvailabilityText.setText(getResources().getString(
					R.string.login_server_online));
			mServerAvailabilityText.setTextColor(getResources().getColor(
					R.color.green));
			mServerTimestamp.setText(MillisecondToDate.getDate(Long
					.valueOf(Timestamps.getTimestampFromServer())));
			// check if database is different
			if (Timestamps.databaseDiviceIsNewerThenServer()) {
				mCheckSyncText.setTextColor(getResources().getColor(
						R.color.yellow));
				mCheckSyncText.setText("Out of Sync");
			} else {
				mCheckSyncText.setTextColor(getResources().getColor(
						R.color.darkgreen));
				mCheckSyncText.setText("Sync");
			}
		} else {
			// if server is not available
			mServerAvailabilityText.setText(getResources().getString(
					R.string.login_server_offline));
			mServerAvailabilityText.setTextColor(getResources().getColor(
					R.color.red));
			mServerTimestamp.setText("");
			mServerText.setText("");
		}
		mDeviceTimestamp.setText(MillisecondToDate.getDate(Long
				.valueOf(Timestamps.getTimestampFromDevice())));

		// changes to email will have an event
		mEmailField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String email = mEmailField.getText().toString();
					mEv = new EmailValidator();
					if (!mEv.validate(email)) {
						Toast.makeText(getApplicationContext(),
								getResources().getString(R.string.login_toast),
								Toast.LENGTH_LONG).show();
					}
					return true;
				}
				return false;
			}
		});

		mPwField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String pw = mPwField.getText().toString();
					if (pw.length() != 6) {
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.passwort_to_short),
								Toast.LENGTH_LONG).show();
					}
					return true;
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
				if (ServerAvailability.isReachable(URLs.getExternalServerIP())) {

					try {
						response = CustomHttpClient.executeHttpPost(
								URLs.getExternalLoginPHP(), postParameters);
						String res = response.toString();
						res = res.replaceAll("\\s+", "");
						if (res.equals("1")) {
							mErrorText.setText(getResources().getString(
									R.string.login_login_accepted));
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.login_hint_download),
									Toast.LENGTH_LONG).show();
							if (Timestamps.databaseDiviceIsNewerThenServer()) {
								// if the device is newer then the server
								IO.exportDatabase();
								Timestamps.exportTimestampToServer();
							} else {
								// if the server is newer then the device
								IO.importDatabase();
								Timestamps.importTimestampFromServer();
							}

							startActivity(new Intent(LoginActivity.this,
									TodoListActivity.class));
						} else {
							mErrorText.setText(getResources().getString(
									R.string.login_login_not_accepted));
							mLogIn.setEnabled(false);
						}
					} catch (Exception e) {
						Log.e("Database", e.toString());
					}
				} else {
					mErrorText.setText(getResources().getString(
							R.string.login_login_server_offline));
				}
			}
		});

		mLogInLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initiatePopupWindow();

			}
		});
	}

	private void updateButtonState() {
		mEv = new EmailValidator();
		boolean enabled = mEv.validate(mEmailField.getText().toString())
				&& checkEditText(mPwField);
		mLogIn.setEnabled(enabled);
	}
	private boolean checkEditText(EditText edit) {
		if (edit.getText().toString().length() == 6)
			return true;
		return false;
	}

	private class LocalTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			updateButtonState();
			mErrorText.setText("");
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

	}

	private void initiatePopupWindow() {

		LayoutInflater inflater = (LayoutInflater) LoginActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popup_window,
				(ViewGroup) findViewById(R.id.popup_element));
		mPw = new PopupWindow(layout, 230, 200, true);
		mPw.setBackgroundDrawable(new BitmapDrawable());
		mPw.showAtLocation(layout, Gravity.CENTER, 0, 0);
		Button cancelButton = (Button) layout
				.findViewById(R.id.end_data_send_button);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPw.dismiss();
				startActivity(new Intent(LoginActivity.this,
						TodoListActivity.class));
			}
		});
	}
}