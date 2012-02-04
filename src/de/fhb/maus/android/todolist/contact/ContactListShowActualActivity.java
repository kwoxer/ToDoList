package de.fhb.maus.android.todolist.contact;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import de.fhb.maus.android.todolist.R;

public class ContactListShowActualActivity extends ListActivity{
	
	private Button addContact;
	private ArrayList<Contact> mContactsList = new ArrayList<Contact>();
	private ArrayAdapter<Contact> mAdapter;
	
	/**
	 * when Activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);

		addContact = (Button) findViewById(R.id.buttonAddContact);
		addContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(ContactListShowActualActivity.this, ContactListShowAllActivity.class), 0);				
			}
		});
		
		Log.v("OnActivityResult in ShowActual", "bin drin");
		ArrayList<Contact> sekContactList = new ArrayList<Contact>();
		mContactsList.clear();
		mContactsList = getIntent().getParcelableArrayListExtra("contactlist");
		
		
		
		if (mContactsList != null) {
			Log.v("OnActivityResult in ShowActual", mContactsList.get(1).getName());
		}else{
			mContactsList = new ArrayList<Contact>();
		}
		
		
		
		showContact();
		registerForContextMenu(getListView());
	}
	
	/**
	 * when new result is coming
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		ArrayList<Contact> sekContactList = new ArrayList<Contact>();
		mContactsList.clear();
		mContactsList = intent.getParcelableArrayListExtra("contactlist");
		if(mContactsList == null){
			Log.v("OnActivityResult in ShowActual", "return");
			return;
		}
		showContact();
	}
	
	public void showContact(){
		mAdapter = new InteractivContactarrayAdapter(this, mContactsList);
		setListAdapter(mAdapter);
	}
}
