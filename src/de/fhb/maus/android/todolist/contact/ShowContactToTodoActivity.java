package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Button;
import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class ShowContactToTodoActivity extends ListActivity{

	private ArrayAdapter<Contact> mAdapter;
	private ArrayList<Contact> mContactsList = new ArrayList<Contact>();
	private Cursor mCursor;
	private TodoDatabaseAdapter mDbHelper;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		
		Button addContact = (Button) findViewById(R.id.buttonAddContact);
		addContact.setVisibility(android.view.View.GONE);
		Button kontacktlist = (Button) findViewById(R.id.actualContactList);
		kontacktlist.setVisibility(android.view.View.GONE);
		mDbHelper = new TodoDatabaseAdapter(this);
		mDbHelper.open();
		showContacts();
		registerForContextMenu(getListView());
	}	
	

	private void showContacts(){
		ArrayList<String> contactIds = new ArrayList<String>(); 
		mAdapter = new InteractivContactarrayAdapter(this, mContactsList, null);
		setListAdapter(mAdapter);
		mCursor = mDbHelper.fetchContactsWithTodo();
		if (!mCursor.isAfterLast()) {
		startManagingCursor(mCursor);
			for(mCursor.moveToFirst();!mCursor.isAfterLast(); mCursor.moveToNext()){
				String contactId = mCursor.getString(mCursor
						.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_CONTACTID));
				contactIds.add(contactId);
			}
		}
		showPhoneContacts(contactIds);
	}
	
	private void showPhoneContacts(ArrayList<String> contactIds) {
		mContactsList.clear();
		if (contactIds.isEmpty()) return;
		Contact mContact;
		
		for(int i = 0 ; i<contactIds.size(); i++){
			String contactId = contactIds.get(i);				
				mContact = new Contact();
	
				/*
				 * query the phones for each contact
				 */
				Cursor names = getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null,
						BaseColumns._ID + " = " + contactId, null, null);
				names.moveToNext();
					String displayName = names
							.getString(names
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					long contactid = names.getLong(names
							.getColumnIndex(BaseColumns._ID));
					mContact.setName(displayName);
					mContact.setContactid(contactid);
				
				names.close();
	
				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
								+ contactId, null, null);
				while (phones.moveToNext()) {

					String phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					mContact.setNumber(phoneNumber);
				}
				phones.close();
	
				Cursor emails = getContentResolver().query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
								+ contactId, null, null);
				while (emails.moveToNext()) {
					String email = emails
							.getString(emails
									.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
					mContact.setEmail(email);
				}
	
				mContactsList.add(mContact);
				emails.close();
		}
	}
}
