package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
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
import de.fhb.maus.android.todolist.R.id;
import de.fhb.maus.android.todolist.R.layout;
import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class ContactEditActivity extends Activity {

	/**
	 * the ui elements
	 */
	private EditText entryName;
	private EditText entryEmail;
	private EditText entryPhone;
	private Button saveEntry;
	private Contact contact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_edit);

		// initialise the ui elements
		entryName = (EditText) findViewById(R.id.entryName);
		entryEmail = (EditText) findViewById(R.id.entryEmail);
		entryPhone = (EditText) findViewById(R.id.entryPhone);
		saveEntry = (Button) findViewById(R.id.saveEntry);

		contact = (Contact) getIntent().getSerializableExtra("contact");
		if (contact != null) {
			String displayName = contact.getName();
			if (displayName != null) {
				Log.v("AddContactActivityGetIntent", displayName);
				entryName.setText(displayName);
				entryName.setEnabled(false);
				saveEntry.setText("bearbeiten");
				String phonenumber = contact.getNumber();
				if (phonenumber != null) {
					entryPhone.setText(phonenumber);
					entryPhone.setEnabled(false);
				}
				String email = contact.getEmail();
				if (email != null) {
					entryEmail.setText(email);
					entryEmail.setEnabled(false);
				}
			}
		}

		// set a listener on the saveEntry button (we do not do any validation
		// here!)
		saveEntry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveEntry();
			}
		});
	}

	private void saveEntry() {
		String editedName = entryName.getText().toString();
		if ("".equals(editedName)) {
			Toast.makeText(ContactEditActivity.this,
					"Ein Name muss eingegeben werden!", Toast.LENGTH_SHORT)
					.show();
		} else {
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			int rawContactInsertIndex = ops.size();

			ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
					.withValue(RawContacts.ACCOUNT_NAME, null)
					.withValue(RawContacts.ACCOUNT_TYPE, null).build());
			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID,
							rawContactInsertIndex)
					.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
					.withValue(StructuredName.DISPLAY_NAME, editedName).build());
			String editedEmail = entryEmail.getText().toString();
			String editedPhone = entryPhone.getText().toString();
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
			if (!"".equals(editedPhone)) {
				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,
								rawContactInsertIndex)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, editedPhone).build());
			}

			try {
				getContentResolver()
						.applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			setResult(RESULT_OK);
			Log.v("AddContactAction", "vor finish()");
			finish();
		}

	}
}
