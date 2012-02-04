package de.fhb.maus.android.todolist.sending;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.contact.Contact;

public class SendingTextActivity extends Activity {

	private Button sending;
	private String mEmail;
	private String mPhone;
	private Contact mContact;
	private EditText text;
	private EditText adress;
	private boolean sendsms;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sending_sms);
		
		sending = (Button) findViewById(R.id.sendingtext);		
		adress = (EditText)findViewById(R.id.contactText);
		text = (EditText)findViewById(R.id.messageText);
		
		mContact = (Contact) getIntent().getSerializableExtra("contact");	
		sendsms = (Boolean)getIntent().getSerializableExtra("sms");
		if(sendsms){
			mPhone = mContact.getNumber();
			adress.setText(mPhone);
			adress.setEnabled(false);
			sending.setText("Send SMS");
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
				}
				
			}
		});
	}
	
	public void sendSMS (String mPhone, String message){
//		PendingIntent pi = PendingIntent.getActivity(SendingTextActivity.this, 0, new Intent(SendingTextActivity.this,SendingTextActivity.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(mPhone, null,message,null,null);
	}
	
	
//	//---sends an SMS message to another device---
//    private void sendSMS(String phoneNumber, String message)
//    {        
//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
// 
//        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
//            new Intent(SENT), 0);
// 
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
//            new Intent(DELIVERED), 0);
// 
//        //---when the SMS has been sent---
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS sent", 
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(getBaseContext(), "Generic failure", 
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), "No service", 
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), "Null PDU", 
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(getBaseContext(), "Radio off", 
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
// 
//        //---when the SMS has been delivered---
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS delivered", 
//                                Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered", 
//                                Toast.LENGTH_SHORT).show();
//                        break;                        
//                }
//            }
//        }, new IntentFilter(DELIVERED));        
// 
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
//    }
	
	
	
	
}
