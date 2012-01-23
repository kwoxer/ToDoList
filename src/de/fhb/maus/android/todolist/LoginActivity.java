package de.fhb.maus.android.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	/** Called when the activity is first created. */

	private Button logIn;
	private boolean toastAlreadyShown = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//TODO just 6 signs long password
		//TODO get ToDo from the server when correkt login
		//TODO check with server if device was offline

		logIn = (Button) findViewById(R.id.login);
		logIn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText emailField = (EditText) findViewById(R.id.email);
				String email = emailField.getText().toString();
				if (email.length() == 0) {
					if (!toastAlreadyShown) {
						Toast toast = Toast
								.makeText(
										getApplicationContext(),
										getResources().getString(R.string.login_toast),
										Toast.LENGTH_SHORT);
						toast.show();
						toastAlreadyShown = true;
						return;
					}
				}
				else
					//TODO Login with server and database here
					startActivity(new Intent(LoginActivity.this,
							TodoListActivity.class));
			}
		});
	}
}