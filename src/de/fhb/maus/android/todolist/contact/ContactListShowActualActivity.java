package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
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
import android.widget.Toast;
import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class ContactListShowActualActivity extends ListActivity {

	private Button addContact;
	private Button backtoContactButton;
	private ArrayList<Contact> mContactsList = new ArrayList<Contact>();
	private ArrayAdapter<Contact> mAdapter;
	private String mRowId;
	private TodoDatabaseAdapter mDbHelper;
	private Cursor mCursor;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	/**
	 * when Activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);

		backtoContactButton = (Button) findViewById(R.id.actualContactList);
		backtoContactButton.setVisibility(8);

		addContact = (Button) findViewById(R.id.buttonAddContact);
		addContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(
						ContactListShowActualActivity.this,
						ContactListShowAllActivity.class), 0);
			}
		});
		mContactsList.clear();
		mContactsList = getIntent().getParcelableArrayListExtra("contactlist");

		if (mContactsList != null) {
		} else {
			mContactsList = new ArrayList<Contact>();
		}
		mRowId = getIntent().getStringExtra("todoRowid");

		if (mRowId != null) {
			Toast.makeText(ContactListShowActualActivity.this,
					"test " + mRowId, Toast.LENGTH_SHORT).show();
			mDbHelper = new TodoDatabaseAdapter(this);
			mDbHelper.open();
			showContact(mRowId);
		}
		showContact(mRowId);
		registerForContextMenu(getListView());
	}

	/**
	 * Delete a Todo by long click on it
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case DELETE_ID :
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
						.getMenuInfo();

				Contact contact = (Contact) getListAdapter().getItem(
						info.position);
				String contactid = String.valueOf(contact.getContactid());

				mDbHelper.deleteContact(mRowId, contactid);
				showContact(mRowId);
				return true;
		}

		return super.onContextItemSelected(item);
	}

	/**
	 * when a contact was normally clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Contact item = (Contact) this.getListAdapter().getItem(position);

		Intent i = new Intent(this, ContactEditActivity.class);
		i.putExtra("contact", item);

		startActivityForResult(i, ACTIVITY_EDIT);
	}
	
	
	/**
	 * When a ToDo Delete Menu is gonna be shown
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.todo_list_delete);
	}

	/**
	 * when new result is coming
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == 0) {
			return;
		}
		mContactsList.clear();
		mContactsList = intent.getParcelableArrayListExtra("contactlist");
		writeContactsToDb();
		showContact(mRowId);

	}

	private void writeContactsToDb() {
		boolean inTable = false;
		String contactId = null;
		String rowId = null;
		mCursor = mDbHelper.fetchAllContacts();
		startManagingCursor(mCursor);
		for (int i = 0; i < mContactsList.size(); i++) {
			mCursor.moveToFirst();
			for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor
					.moveToNext()) {
				contactId = mCursor.getString(mCursor
						.getColumnIndex(TodoDatabaseAdapter.KEY_CONTACTID));
				rowId = mCursor.getString(mCursor
						.getColumnIndex(TodoDatabaseAdapter.KEY_ROWID));
				if (String.valueOf(mContactsList.get(i).getContactid()).equals(
						contactId)
						&& this.mRowId.equals(rowId)) {
					inTable = true;
				}
			}
			if (!inTable) {
				mDbHelper.setContact(
						String.valueOf(mContactsList.get(i).getContactid()),
						this.mRowId);
			}
		}

	}

	private void showContact(String rowId) {
		ArrayList<String> contactIds = new ArrayList<String>();
		mAdapter = new InteractivContactarrayAdapter(this, mContactsList, rowId);
		setListAdapter(mAdapter);
		mCursor = mDbHelper.fetchContacts(rowId);
		if (rowId != null && !mCursor.isAfterLast()) {
			startManagingCursor(mCursor);
			for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor
					.moveToNext()) {
				String contactId = mCursor
						.getString(mCursor
								.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_CONTACTID));
				contactIds.add(contactId);
			}
		}
		showPhoneContacts(contactIds);
	}

	/**
	 * refreshes contact to out listview
	 */
	private void showPhoneContacts(ArrayList<String> contactIds) {
		mContactsList.clear();
		if (contactIds.isEmpty())
			return;
		Contact mContact;

		for (int i = 0; i < contactIds.size(); i++) {
			String contactId = contactIds.get(i);

			mContact = new Contact();

			/*
			 * query the phones for each contact
			 */
			Cursor names = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null,
					BaseColumns._ID + " = " + contactId, null, null);
			names.moveToNext();
			String displayName = names.getString(names
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
