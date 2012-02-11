package de.fhb.maus.android.todolist.sending;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.contact.Contact;
import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class SendingTextActivity extends Activity {

	private Button sending;
	private String mEmail;
	private String mPhone;
	private Contact mContact;
	private EditText text;
	private EditText adress;
	private TodoDatabaseAdapter mDbHelper;
	private String rowId = null; 
	private boolean sendsms;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sending_sms);
		
		mDbHelper = new TodoDatabaseAdapter(this);
		mDbHelper.open();
		
		sending = (Button) findViewById(R.id.sendingtext);		
		adress = (EditText)findViewById(R.id.contactText);
		text = (EditText)findViewById(R.id.messageText);
		
		mContact = (Contact) getIntent().getParcelableExtra("contact");	
		sendsms = (Boolean)getIntent().getSerializableExtra("sms");
		rowId = (String)getIntent().getStringExtra("rowId");
		if(sendsms){
			mPhone = mContact.getNumber();
			adress.setText(mPhone);
			adress.setEnabled(false);
			sending.setText("Send SMS");
			if(rowId!= null) queryTodoInfo();
		}else{
			mEmail = mContact.getEmail();
			adress.setText(mEmail);
			adress.setEnabled(false);
			sending.setText("Send E-mail");
		}		
		
		
		sending.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v){
				String  inputmessage = text.getText().toString();
				if(sendsms && inputmessage.length() >0){
					sendSMS(mPhone,inputmessage);
					return;
				}
				if(!sendsms && inputmessage.length() >0){
					sendEmail(mPhone, inputmessage);
					Toast.makeText(getBaseContext(), "email sent", 
                          Toast.LENGTH_SHORT).show();
					return;					
				}
			}
		});
	}
	
	private void queryTodoInfo(){
		String[] info = new String[2];
		Cursor mCursor;
//		mCursor = mDbHelper.fetchspezificTodo(rowId,String.valueOf(mContact.getContactid()));
		mCursor = mDbHelper.fetchTodo(Long.valueOf(rowId));
		info[0] = mCursor.getString(mCursor.getColumnIndex(TodoDatabaseAdapter.KEY_SUMMARY));
		info[1] = mCursor.getString(mCursor.getColumnIndex(TodoDatabaseAdapter.KEY_DESCRIPTION));
		text.setText("Titel= " + info[0] + "\nBeschreibung= " + info[1]);
	}
	
	private void sendEmail (String mPhone,String message){
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plane/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mPhone});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "iche bins");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		startActivity(Intent.createChooser(emailIntent, "send mail..."));
		finish();
	}
	
	private void sendSMS (String mPhone, String message){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(mPhone, null,message,null,null);
		finish();
	}	
	/**
	 * When activity is goona be destroyed
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}
}
