package de.fhb.maus.android.todolist.contact;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.fhb.maus.android.todolist.R;

public class SendingTextActivity extends Activity {

	private Button sending;
	private EditText mName;
	private EditText mEmail;
	private EditText mPhone;
	private Contact mContact;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sending_sms);
		
		mContact = (Contact) getIntent().getSerializableExtra("contact");		
		
		
		sending = (Button) findViewById(R.id.sendingtext);
		sending.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("buttonclick",mContact.getName());				
			}
		});
	}
}
