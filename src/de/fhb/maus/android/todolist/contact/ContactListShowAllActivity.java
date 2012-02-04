package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import de.fhb.maus.android.todolist.R;

public class ContactListShowAllActivity extends ListActivity {
	//private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private ArrayList<Contact> mContactsList = new ArrayList<Contact>();
	private ArrayAdapter<Contact> mAdapter;
	private Contact mContact;
	/**
	 * when Activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);

		Button addContact = (Button) findViewById(R.id.buttonAddContact);
		addContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(ContactListShowAllActivity.this,
						ContactEditActivity.class), ACTIVITY_EDIT);
			}
		});
		//TODO hier steigt er aus, warum zeigt er die kontakte nimma an?
		showPhoneContacts();
		registerForContextMenu(getListView());
	}

	/**
	 *  Delete a Todo by long click on it 
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case DELETE_ID :
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
						.getMenuInfo();

				Contact contact = (Contact) getListAdapter().getItem(
						info.position);
				long contactid = contact.getContactid();

				Log.v("displayname", contact.getName());
				Log.v("contactid", String.valueOf(contactid));

				ContentResolver cr = getContentResolver();
				String[] params = new String[]{String.valueOf(contactid)};

				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation
						.newDelete(ContactsContract.RawContacts.CONTENT_URI)
						.withSelection(BaseColumns._ID + "=?", params).build());
				try {
					cr.applyBatch(ContactsContract.AUTHORITY, ops);
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				mAdapter.clear();
				showPhoneContacts();
				return true;
		}

		return super.onContextItemSelected(item);
	}
	/**
	 *  When a ToDo Delete Menu is gonna be shown
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.todo_list_delete);
	}

	/**
	 * when a contact was normally clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Toast.makeText(this, item + "selected", Toast.LENGTH_LONG).show();
		Contact item = (Contact) this.getListAdapter().getItem(position);

		// String keyword = item.getName();
		// Toast.makeText(this, keyword, Toast.LENGTH_SHORT).show();

		Intent i = new Intent(this, ContactEditActivity.class);
		i.putExtra("contact", item);

		startActivityForResult(i, ACTIVITY_EDIT);
	}
	/**
	 * when new result is coming
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		mAdapter.clear();
		showPhoneContacts();
	}
	/**
	 * refreshes contact to out listview
	 */
	private void showPhoneContacts() {
		mAdapter = new InteractivContactarrayAdapter(this, mContactsList);
		setListAdapter(mAdapter);
		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor
					.getColumnIndex(BaseColumns._ID));
			mContact = new Contact();

			/*
			 * query the phones for each contact
			 */
			Cursor names = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null,
					BaseColumns._ID + " = " + contactId, null, null);
			while (names.moveToNext()) {
				String displayName = names
						.getString(names
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				Log.v("showcontact", "displayname");
				long contactid = names.getLong(names
						.getColumnIndex(BaseColumns._ID));
				Log.v("showcontact", "displayname2");
				mContact.setName(displayName);
				mContact.setContactid(contactid);
			}
			Log.v("showcontact", "displayname3");
			names.close();

			Cursor phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			Log.v("showcontact", "displayname4");
			while (phones.moveToNext()) {
				Log.v("showcontact", "displayname5");
				String phoneNumber = phones
						.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				mContact.setNumber(phoneNumber);
				Log.v("showcontact", "displayname6");
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
	
	private ArrayList<Contact> getCheckedContacts(){
		ArrayList<Contact> selectedContacts = new ArrayList<Contact>();
		Log.v("size()",String.valueOf(mContactsList.size()));
		for(int i = 0; i<mContactsList.size();i++){
			if(mContactsList.get(i).isSelected()){
				selectedContacts.add(mContactsList.get(i));
			}			
		}
		return selectedContacts;
	}
	
	
	@Override
	public void onBackPressed(){
		Log.v("onbackPressed in ShowAll BackPressed", "bin drin");

		Intent intent = new Intent(ContactListShowAllActivity.this, ContactListShowActualActivity.class);
		intent.putParcelableArrayListExtra("contactlist", getCheckedContacts());
		setResult(RESULT_OK, intent);
		finish();
	}
	
}
