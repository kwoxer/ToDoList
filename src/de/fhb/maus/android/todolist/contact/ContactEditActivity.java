package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.fhb.maus.android.todolist.R;

public class ContactEditActivity extends Activity {

	private EditText mName;
	private EditText mEmail;
	private EditText mPhone;
	private Button mSave;
	private Contact mContact;
	private String displayName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_edit);

		// initialise the ui elements
		mName = (EditText) findViewById(R.id.editTextName);
		mPhone = (EditText) findViewById(R.id.editTextPhone);
		mEmail = (EditText) findViewById(R.id.editTextEmail);
		mSave = (Button) findViewById(R.id.buttonSave);

		//get the actual contact 
		mContact = (Contact) getIntent().getParcelableExtra("contact");
		if (mContact != null) {
			displayName = mContact.getName();
			// set the enabletype if he had input data
			if (displayName != null) {
				mName.setText(displayName);
				mName.setEnabled(false);
				mSave.setText(getResources().getString(R.string.contact_edit_button_edit));
				String phonenumber = mContact.getNumber();
				if (phonenumber != null) {
					mPhone.setText(phonenumber);
					mPhone.setEnabled(false);
				}
				String email = mContact.getEmail();
				if (email != null) {
					mEmail.setText(email);
					mEmail.setEnabled(false);
				}
			}
		}
		
		//save the entry if the button is clicked
		mSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveEntry();
			}
		});
	}

	/**
	 * put the entries into the local adressbook
	 */
	private void saveEntry() {
		String editedName = mName.getText().toString();
		if ("".equals(editedName)) {
			Toast.makeText(ContactEditActivity.this,
					getResources().getString(R.string.contact_edit_button_save_entry), Toast.LENGTH_SHORT)
					.show();
		} else {
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			int rawContactInsertIndex = ops.size();
			//put the displayname into the displaynametabel
			if(displayName==null){
				ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
						.withValue(RawContacts.ACCOUNT_NAME, null)
						.withValue(RawContacts.ACCOUNT_TYPE, null).build());
				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
						.withValue(StructuredName.DISPLAY_NAME, editedName).build());	
			}
			String editedEmail = mEmail.getText().toString();
			String editedPhone = mPhone.getText().toString();
			//put the phonenumber into the numbertabel
			if (!"".equals(editedPhone)) {
				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, editedPhone).build());
			}
			//put the emiladress into the emailadresstabel
			if (!"".equals(editedEmail)) {
				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(
								Data.MIMETYPE,
								ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Email.DATA,
								editedEmail).build());

			}
			try {
				getContentResolver()
						.applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
			setResult(RESULT_OK);
			finish();
		}
	}
}
