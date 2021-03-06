package de.fhb.maus.android.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.fhb.maus.android.todolist.contact.Contact;
import de.fhb.maus.android.todolist.contact.ContactListShowActualActivity;
import de.fhb.maus.android.todolist.contact.ShowContactToTodoActivity;
import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;
import de.fhb.maus.android.todolist.helpers.PATHs;
import de.fhb.maus.android.todolist.helpers.URLs;
import de.fhb.maus.android.todolist.io.IO;
import de.fhb.maus.android.todolist.server.ServerAvailability;
import de.fhb.maus.android.todolist.timestamp.Timestamps;

/**
 * @author Curtis & Sebastian
 * @version v0.4
 */
public class TodoListActivity extends ListActivity {
	private TodoDatabaseAdapter mDbHelper;
	// private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private Cursor mCursor;
	private Button mAdd, mLogout, showContactsWithTodo;
	private int order = 0;
	private long cid = -1;
	private String reminder, urgent;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);

		showContactsWithTodo = (Button) findViewById(R.id.showContactsWithToDO);
		showContactsWithTodo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TodoListActivity.this,
						ShowContactToTodoActivity.class));
			}
		});

		// Sets up Button for adding a ToDo
		mAdd = (Button) findViewById(R.id.buttonAdd);
		mAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TodoListActivity.this,
						TodoEditActivity.class));
			}
		});
		// Sets up Button showing the logout Toast
		mLogout = (Button) findViewById(R.id.buttonLogout);
		mLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Timestamps.updateTimestampOnDevice();
				if (ServerAvailability.isReachable(URLs.getExternalServerIP())){
					// when device is online send database and timestamp
					IO.exportDatabase();
					Timestamps.exportTimestampToServer();
					Toast.makeText(TodoListActivity.this,
							getResources().getString(R.string.additionalLoggedOut),
							Toast.LENGTH_SHORT).show();
				}else
					Toast.makeText(TodoListActivity.this,
							getResources().getString(R.string.additionalLoggedOutOffline),
							Toast.LENGTH_SHORT).show();
				startActivity(new Intent(TodoListActivity.this,
						LoginActivity.class));
			}
		});

		// get contactId to show individual Todos
		Contact contact = (Contact) getIntent().getParcelableExtra("contact");
		if (contact != null) {
			cid = contact.getContactid();
		}

		// Divides the ToDo with a line
		this.getListView().setDividerHeight(2);
		// Helps to get our data from a database
		mDbHelper = new TodoDatabaseAdapter(this);
		mDbHelper.open();

		reminder = getResources().getStringArray(R.array.priorities)[0];
		urgent = getResources().getStringArray(R.array.priorities)[1];
		fillToDoList();
		registerForContextMenu(getListView());
	}

	/**
	 * Create the menu based on the XML defintion
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Reaction to the menu selection
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.orderByDone :
				order = 0;
				fillToDoList();
				return true;
			case R.id.orderByDate :
				order = 1;
				fillToDoList();
				return true;
			case R.id.orderByCategory :
				order = 2;
				fillToDoList();
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
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
				mDbHelper.deleteTodo(info.id);
				fillToDoList();
				return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * ToDo was clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, TodoEditActivity.class);
		i.putExtra(TodoDatabaseAdapter.KEY_ROWID, id);
		// Activity returns an result if called with startActivityForResult
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	/**
	 * Called with the result of the other activity requestCode was the origin
	 * request code send to the activity resultCode is the return code, 0 is
	 * everything is ok intend can be use to get some data from the caller
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillToDoList();
	}

	private String getWhereClauseFromCursor(Cursor mCursor) {
		ArrayList<String> list = new ArrayList<String>();
		for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor
				.moveToNext()) {
			list.add(mCursor.getString(mCursor
					.getColumnIndex(TodoDatabaseAdapter.KEY_ROWID)));
		}

		String where = "";
		for (int i = 0; i < list.size(); i++) {
			where = where + TodoDatabaseAdapter.KEY_ROWID + " =" + list.get(i)
					+ " ";
			if (i != (list.size() - 1))
				where = where + " OR ";
		}
		return where;
	}

	/**
	 * Always called when the ToDos are shown
	 */
	private void fillToDoList() {
		if (cid == -1) {
			switch (order) {
				case 0 :
					mCursor = mDbHelper.fetchAllTodosOrderByDone();
					break;
				case 1 :
					mCursor = mDbHelper.fetchAllTodosOrderByDate();
					break;
				case 2 :
					mCursor = mDbHelper.fetchAllTodosOrderByCategory();
					break;
			}
		} else {
			switch (order) {
				case 0 :
					mCursor = mDbHelper.fetchTodoToContacts(cid);
					mCursor = mDbHelper
							.fetchIndividualTodosOrderByDone(getWhereClauseFromCursor(mCursor));
					break;
				case 1 :
					mCursor = mDbHelper.fetchTodoToContacts(cid);
					mCursor = mDbHelper
							.fetchIndividualTodosOrderByDate(getWhereClauseFromCursor(mCursor));
					break;
				case 2 :
					mCursor = mDbHelper.fetchTodoToContacts(cid);
					mCursor = mDbHelper
							.fetchIndividualTodosOrderByCategory(getWhereClauseFromCursor(mCursor));
					break;
			}
		}
		startManagingCursor(mCursor);

		String[] from = new String[]{TodoDatabaseAdapter.KEY_CATEGORY,
				TodoDatabaseAdapter.KEY_DONE, TodoDatabaseAdapter.KEY_DATE,
				TodoDatabaseAdapter.KEY_SUMMARY, TodoDatabaseAdapter.KEY_ROWID};
		int[] to = new int[]{R.id.imageViewIcon, R.id.todoRowCheckBox,
				R.id.textViewDate, R.id.textViewSummary, R.id.buttonContacts};

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter column = new SimpleCursorAdapter(this,
				R.layout.todo_row, mCursor, from, to);

		// Updating Items
		column.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			// Go through Cursor Adapter and watch
			@Override
			public boolean setViewValue(View view, final Cursor cursor,
					final int columnIndex) {

				// Update Button
				if (view instanceof Button) {
					Button test = (Button) view;
					final String id = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_ROWID));
					test.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(TodoListActivity.this,
									ContactListShowActualActivity.class);
							intent.putExtra("todoRowid", id);
							startActivity(intent);
						}
					});
				}

				// Update Icon
				final int nCheckedIndexIcon = (cursor
						.getColumnIndex(TodoDatabaseAdapter.KEY_CATEGORY));
				if (columnIndex == nCheckedIndexIcon) {
					final ImageView image = (ImageView) view;
					String category = cursor.getString(nCheckedIndexIcon);
					final long id = cursor.getLong(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_ROWID));
					final String date = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_DATE));
					final int done = cursor.getInt(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_CATEGORY));
					final String summary = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_SUMMARY));
					final String description = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_DESCRIPTION));
					if (category.equals(reminder)) {
						image.setTag(reminder);
						image.setImageResource(R.drawable.ic_todo);
					} else {
						image.setTag(urgent);
						image.setImageResource(R.drawable.ic_todoimportant);
					}
					image.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							boolean bla;
							if (done == 1)
								bla = true;
							else
								bla = false;
							String imageTag = (String) image.getTag();
							if (imageTag.equals(reminder)) {
								image.setTag(urgent);
								image.setImageResource(R.drawable.ic_todoimportant);
								mDbHelper.updateTodo(id, date, urgent, bla,
										summary, description);
							} else {
								image.setTag(reminder);
								image.setImageResource(R.drawable.ic_todo);
								mDbHelper.updateTodo(id, date, reminder, bla,
										summary, description);
							}
						}
					});
					return true;
				}

				// Update Checkbox
				int nCheckedIndexCheckbox = (cursor
						.getColumnIndex(TodoDatabaseAdapter.KEY_DONE));
				if (columnIndex == nCheckedIndexCheckbox) {
					CheckBox cb = (CheckBox) view;
					boolean bChecked = (cursor.getInt(nCheckedIndexCheckbox) != 0);
					final long id = cursor.getLong(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_ROWID));
					final String date = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_DATE));
					final String category = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_CATEGORY));
					final String summary = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_SUMMARY));
					final String description = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_DESCRIPTION));
					cb.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							CheckBox mCheckBox = (CheckBox) v;
							if (mCheckBox.isChecked())
								mDbHelper.updateTodo(id, date, category, true,
										summary, description);
							else
								mDbHelper.updateTodo(id, date, category, false,
										summary, description);
						}
					});
					cb.setChecked(bChecked);
					return true;
				}

				// Update Date
				int nCheckedIndexDate = (cursor
						.getColumnIndex(TodoDatabaseAdapter.KEY_DATE));
				if (columnIndex == nCheckedIndexDate) {
					TextView dateTextView = (TextView) view;
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
					String gmtTime = dateFormat.format(Long.valueOf(cursor
							.getString(nCheckedIndexDate)));
					dateTextView.setText(gmtTime);
					return true;
				}

				// Update Summary Color
				final int nCheckedIndexColor = (cursor
						.getColumnIndex(TodoDatabaseAdapter.KEY_SUMMARY));
				if (columnIndex == nCheckedIndexColor) {
					TextView summaryTextView = (TextView) view;

					summaryTextView.setText(cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_SUMMARY)));
					String todoTimeInSoconds;
					todoTimeInSoconds = cursor.getString(cursor
							.getColumnIndex(TodoDatabaseAdapter.KEY_DATE));
					if (Long.valueOf(todoTimeInSoconds)
							- System.currentTimeMillis() < 0)
						summaryTextView.setTextColor(getResources().getColor(
								R.color.red));
					else if (Long.valueOf(todoTimeInSoconds)
							- System.currentTimeMillis() <= 86400)
						// 1 day
						summaryTextView.setTextColor(getResources().getColor(
								R.color.green));
					else
						summaryTextView.setTextColor(getResources().getColor(
								R.color.white));

					return true;
				}
				return false;
			}
		});
		setListAdapter(column);
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
