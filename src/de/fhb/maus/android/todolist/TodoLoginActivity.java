package de.fhb.maus.android.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TodoLoginActivity extends Activity {
	/** Called when the activity is first created. */

	private Button signIn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		signIn = (Button) findViewById(R.id.SignIn);
		signIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText emailField = (EditText) findViewById(R.id.email);
				String email = emailField.getText().toString();
				if (email.length() == 0) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Du musst eine Eingabe in das Emailfeld eingeben.",
							Toast.LENGTH_SHORT);
					toast.show();
					return;
				}

				// Log.i(ShowToDoActivity.class.getName(),)
				startActivity(new Intent(TodoLoginActivity.this,
						TodoListActivity.class));
			}

		});
	}
}