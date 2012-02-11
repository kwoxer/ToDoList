package de.fhb.maus.android.todolist;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import de.fhb.maus.android.todolist.database.CustomHttpClient;
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

	private Button mLogIn;
	private boolean toastAlreadyShown = false;
	private EditText emailField, pwField;
	private TextView error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		emailField = (EditText) findViewById(R.id.editTextEmail);
		pwField = (EditText) findViewById(R.id.editTextPassword);
		mLogIn = (Button) findViewById(R.id.buttonLogin);
		error = (TextView) findViewById(R.id.textViewError);

		mLogIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = emailField.getText().toString();
				String password = pwField.getText().toString();

				if (username.length() == 0) {
					if (!toastAlreadyShown) {
						Toast toast = Toast.makeText(getApplicationContext(),
								getResources().getString(R.string.login_toast),
								Toast.LENGTH_SHORT);
						toast.show();
						toastAlreadyShown = true;
						return;
					}
				} else {
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
						if (res.equals("1"))
							startActivity(new Intent(LoginActivity.this,
									TodoListActivity.class));
						else
							error.setText("0");
					} catch (Exception e) {
						Log.e("Database", e.toString());
					}

				}
			}
		});
	}
}