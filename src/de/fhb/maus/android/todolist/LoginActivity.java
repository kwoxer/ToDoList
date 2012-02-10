package de.fhb.maus.android.todolist;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import de.fhb.maus.android.todolist.database.CustomHttpClient;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	private TextView error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// TODO get ToDo from the server when correct login
		// TODO check with server if device was offline
		// TODO Übergang mit delay, also Server delay einplanen 3000 ms

		mLogIn = (Button) findViewById(R.id.buttonLogin);
		mLogIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText emailField = (EditText) findViewById(R.id.editTextEmail);
				EditText pwField = (EditText) findViewById(R.id.editTextPassword);

				String username = emailField.getText().toString();
				String password = pwField.getText().toString();

				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair("mail", username));
				postParameters.add(new BasicNameValuePair("pwhash", password));

				String response = null;
				try {
					response = CustomHttpClient.executeHttpPost(
							"http://10.0.2.2:8080/login.php?", postParameters);

					String res = response.toString();
					res = res.replaceAll("\\s+", "");
					if (res.equals("1"))
						error.setText("Correct Username or Password");
					else
						error.setText("Sorry!! Incorrect Username or Password");
				} catch (Exception e) {
					emailField.setText(e.toString());
				}

				//
				// try {
				// if (username.length() > 0 && password.length() > 0) {
				// DBUserAdapter dbUser = new DBUserAdapter(
				// LoginActivity.this);
				// dbUser.open();
				// //dbUser.AddUser(username, password);
				//
				// if (dbUser.Login(username, password)) {
				// Toast.makeText(LoginActivity.this,
				// "Successfully Logged In", Toast.LENGTH_LONG)
				// .show();
				// startActivity(new Intent(LoginActivity.this,
				// TodoListActivity.class));
				// } else {
				// Toast.makeText(LoginActivity.this,
				// "Invalid Username/Password",
				// Toast.LENGTH_LONG).show();
				// }
				// dbUser.close();
				// }
				//
				// } catch (Exception e) {
				// Toast.makeText(LoginActivity.this, e.getMessage(),
				// Toast.LENGTH_LONG).show();
				// }

				// String email = emailField.getText().toString();
				// if (email.length() == 0) {
				// if (!toastAlreadyShown) {
				// Toast toast = Toast.makeText(getApplicationContext(),
				// getResources().getString(R.string.login_toast),
				// Toast.LENGTH_SHORT);
				// toast.show();
				// toastAlreadyShown = true;
				// return;
				// }
				// } else
				// // TODO Login with server and database here
				// startActivity(new Intent(LoginActivity.this,
				// TodoListActivity.class));
			}
		});
	}
}