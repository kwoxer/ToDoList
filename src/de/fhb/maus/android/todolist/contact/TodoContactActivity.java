package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;
import java.util.List;

import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.R.id;
import de.fhb.maus.android.todolist.R.layout;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class TodoContactActivity extends ListActivity{
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private List<Contact> contactsList = new ArrayList<Contact>();
	private ArrayAdapter<Contact> adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);
		
		Button addContact = (Button) findViewById(R.id.addContact);
		addContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivity(new Intent(TodoContactActivity.this,
//						AddContactActivity.class));
				
				startActivityForResult(new Intent(TodoContactActivity.this,
						AddContactActivity.class), ACTIVITY_EDIT);
				
			}
		});
		
		
		showPhoneContacts();
		adapter = new InteractivContactarrayAdapter(this,contactsList);
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l, v, position, id);
//		Toast.makeText(this, item + "selected", Toast.LENGTH_LONG).show();
		Contact item =(Contact) this.getListAdapter().getItem(position);
		
//		String keyword = item.getName();
//		Toast.makeText(this, keyword, Toast.LENGTH_SHORT).show();		
		
		Intent i = new Intent(this, AddContactActivity.class);
		i.putExtra("contact", item);
		
		startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		adapter.clear();
		showPhoneContacts();		
		adapter = new InteractivContactarrayAdapter(this,contactsList);
		setListAdapter(adapter);
		
	}
	private void showPhoneContacts(){
		Contact contact;

		Cursor cursor = getContentResolver().query(
			ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor
					.getColumnIndex(BaseColumns._ID));
			
			contact = new Contact();
			
			/*
			 * query the phones for each contact
			 */
			Cursor names = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					null,
					ContactsContract.Contacts._ID
							+ " = " + contactId, null, null);
			while (names.moveToNext()) {
				Log.v("phoneNumber", "while oben drin");
				String displayName = names
						.getString(names
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				contact.setName(displayName);
				Log.v("phoneNumber", "nach display_name");
			}
			names.close();
			
			/*
			 * query the phones for each contact
			 */
			Cursor phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID
							+ " = " + contactId, null, null);
			while(phones.moveToNext()){
				String phoneNumber = phones
						.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				contact.setNumber(phoneNumber);
				Log.v("phoneNumber", "nach number");
			}
			phones.close();
			
			/*
			 * query the emails for each contact
			 */
			Log.v("phoneNumber", "vor emailcursor");
			Cursor emails = getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID
						+ " = " + contactId, null, null);
			Log.v("phoneNumber", "mitte");
			while(emails.moveToNext()){
				String email = emails
						.getString(emails
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				contact.setNumber(email);
			}				
				contactsList.add(contact);
				Log.v("phoneNumber", "unten");
			

		}
	}
}


