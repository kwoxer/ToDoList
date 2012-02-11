package de.fhb.maus.android.todolist;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import de.fhb.maus.android.todolist.database.CustomHttpClient;
import de.fhb.maus.android.todolist.validator.EmailValidator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	/** Called when the activity is first created. */

	private Button mLogIn, mExit;
	private boolean toastAlreadyShown = false;
	private EditText emailField, pwField;
	private TextView error;
	private EmailValidator ev;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		emailField = (EditText) findViewById(R.id.editTextEmail);
		pwField = (EditText) findViewById(R.id.editTextPassword);
		mLogIn = (Button) findViewById(R.id.buttonLogin);
		error = (TextView) findViewById(R.id.textViewError);
		mExit = (Button) findViewById(R.id.buttonExit);

		mExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this,
						TodoListActivity.class));
			}
		});

		mLogIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = emailField.getText().toString();
				String password = pwField.getText().toString();
				ev = new EmailValidator();
				if (ev.validate(username)) {
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters
							.add(new BasicNameValuePair("name", username));
					postParameters.add(new BasicNameValuePair("pw", password));
					String response = null;
					try {
						response = CustomHttpClient.executeHttpPost(
								"http://10.0.2.2/login/login.php",
								postParameters);
						String res = response.toString();
						res = res.replaceAll("\\s+", "");
						if (res.equals("1")) {
							error.setText("Login accepted");
							startActivity(new Intent(LoginActivity.this,
									TodoListActivity.class));
						} else {
							error.setText("Login not accepted");
						}
					} catch (Exception e) {
						Log.e("Database", e.toString());
					}
				} else {
					if (!toastAlreadyShown) {
						Toast toast = Toast.makeText(getApplicationContext(),
								getResources().getString(R.string.login_toast),
								Toast.LENGTH_SHORT);
						toast.show();
						toastAlreadyShown = true;
					}
				}
			}
		});
	}
}