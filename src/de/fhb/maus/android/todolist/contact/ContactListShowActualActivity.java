package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.TodoListActivity;
import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class ContactListShowActualActivity extends ListActivity{
	
	private Button addContact;
	private Button backtoContactButton;
	private ArrayList<Contact> mContactsList = new ArrayList<Contact>();
	private ArrayAdapter<Contact> mAdapter;
	private String rowId;
	private TodoDatabaseAdapter mDbHelper;
	private Cursor mCursor;
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
				startActivityForResult(new Intent(ContactListShowActualActivity.this, ContactListShowAllActivity.class), 0);				
			}
		});
		Log.v("showcontact", "here we go");
		mContactsList.clear();
		mContactsList = getIntent().getParcelableArrayListExtra("contactlist");
		
		if (mContactsList != null) {
			Log.v("OnActivityResult in ShowActual", mContactsList.get(1).getName());
		}else{
			mContactsList = new ArrayList<Contact>();
		}
		Log.v("showcontact", "here we go");
		rowId = getIntent().getStringExtra("todoRowid");
		
		if(rowId != null){
			Toast.makeText(ContactListShowActualActivity.this, "test " + rowId,
					Toast.LENGTH_SHORT).show();
			mDbHelper = new TodoDatabaseAdapter(this);
			mDbHelper.open();
			showContact(rowId);
		}
	
//		showContact();
		Log.v("rowId beim create", this.rowId) ;
		showContact(rowId);
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
				String contactid = String.valueOf(contact.getContactid());

//				Log.v("displayname", contact.getName());
//				Log.v("contactid", String.valueOf(contactid));
				mDbHelper.deleteContact(rowId, contactid);
//				mAdapter.clear();
				showContact(rowId);
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
	 * when new result is coming
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
//		ArrayList<Contact> sekContactList = new ArrayList<Contact>();
		if(resultCode == 0){return ;
		}
		mContactsList.clear();
		mContactsList = intent.getParcelableArrayListExtra("contactlist");
		writeContactsToDb();
		showContact(rowId);
		
	}
	
	private void writeContactsToDb(){
		boolean inTable =false;
		String contactId = null;
		String rowId = null;
		mCursor = mDbHelper.fetchAllContacts();
		startManagingCursor(mCursor);
		for(int i=0; i<mContactsList.size(); i++ ){
			mCursor.moveToFirst();
			Log.v("inForschleife", "inforschöleife") ;
			for(mCursor.moveToFirst();!mCursor.isAfterLast(); mCursor.moveToNext()){
				contactId = mCursor.getString(mCursor.getColumnIndex(TodoDatabaseAdapter.KEY_CONTACTID));
				rowId = mCursor.getString(mCursor.getColumnIndex(TodoDatabaseAdapter.KEY_ROWID));
				if(String.valueOf(mContactsList.get(i).getContactid()).equals(contactId) && this.rowId.equals(rowId)){		
					inTable = true;
				}				
			}		
			if(!inTable){
				Log.v("insert", "insert contactId=" +String.valueOf(mContactsList.get(i).getContactid())+" rowId"+this.rowId) ;
				mDbHelper.setContact(String.valueOf(mContactsList.get(i).getContactid()), this.rowId);
			}
		}
		
	}
	
	private void showContact(String rowId){
		ArrayList<String> contactIds = new ArrayList<String>(); 
		mAdapter = new InteractivContactarrayAdapter(this, mContactsList);
		setListAdapter(mAdapter);
		mCursor = mDbHelper.fetchContacts(rowId);
		if (rowId != null && !mCursor.isAfterLast()) {
//			Log.v("Daisser inner Ifanweisung mit isAfterLast()", "bin ick drin oder watt");
		startManagingCursor(mCursor);
			for(mCursor.moveToFirst();!mCursor.isAfterLast(); mCursor.moveToNext()){
				String contactId = mCursor.getString(mCursor
						.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_CONTACTID));
//				String TabelrowId = mCursor.getString(mCursor
//						.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_ROWID));
				Log.v("showContact", contactId);
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
		if (contactIds.isEmpty()) return;
		Contact mContact;
		
//		Cursor cursor = getContentResolver().query(
//				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		for(int i = 0 ; i<contactIds.size(); i++){
			String contactId = contactIds.get(i);
			Log.v("shoePhoneContactId=", contactId);
//			cursor.moveToFirst();
//			while (cursor.moveToNext()) {
	//			String contactId = cursor.getString(cursor
	//					.getColumnIndex(BaseColumns._ID));
				
				mContact = new Contact();
	
				/*
				 * query the phones for each contact
				 */
				Cursor names = getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null,
						BaseColumns._ID + " = " + contactId, null, null);
				names.moveToNext();
//				while (names.moveToNext()) {
					String displayName = names
							.getString(names
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					Log.v("displayName", displayName);
					long contactid = names.getLong(names
							.getColumnIndex(BaseColumns._ID));
					mContact.setName(displayName);
					mContact.setContactid(contactid);
//				}
				
				names.close();
	
				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
								+ contactId, null, null);
//				Log.v("showcontact", "displayname4");
				while (phones.moveToNext()) {
//					Log.v("showcontact", "displayname5");
					String phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					mContact.setNumber(phoneNumber);
//					Log.v("showcontact", "displayname6");
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
//			}
		}
	}
}
