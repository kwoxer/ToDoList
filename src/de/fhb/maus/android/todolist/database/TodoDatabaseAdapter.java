package de.fhb.maus.android.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TodoDatabaseAdapter {

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CATEGORY = "category";
	// Summary of the todo
	public static final String KEY_SUMMARY = "summary";
	// Checked when done
	public static final String KEY_DONE = "done";
	// Full description
	public static final String KEY_DESCRIPTION = "description";
	private static final String DATABASE_TABLE = "todo";
	private Context context;
	private SQLiteDatabase database;
	private TodoDatabaseHelper dbHelper;

	public TodoDatabaseAdapter(Context context) {
		this.context = context;
	}

	public TodoDatabaseAdapter open() throws SQLException {
		dbHelper = new TodoDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long createTodo(String category, boolean done, String summary,
			String description) {
		ContentValues initialValues = createContentValues(category, done,
				summary, description);

		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Update the todo
	 */
	public boolean updateTodo(long rowId, String category, boolean done,
			String summary, String description) {
		ContentValues updateValues = createContentValues(category, done,
				summary, description);
		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	/**
	 * Deletes todo
	 */
	public boolean deleteTodo(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all todo in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllTodos() {
		return database.query(DATABASE_TABLE, new String[]{KEY_ROWID,
				KEY_CATEGORY, KEY_DONE, KEY_SUMMARY, KEY_DESCRIPTION}, null,
				null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the defined todo
	 */
	public Cursor fetchTodo(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE,
				new String[]{KEY_ROWID, KEY_CATEGORY, KEY_DONE, KEY_SUMMARY,
						KEY_DESCRIPTION}, KEY_ROWID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String category, boolean done,
			String summary, String description) {
		ContentValues values = new ContentValues();
		values.put(KEY_CATEGORY, category);
		values.put(KEY_DONE, done);
		values.put(KEY_SUMMARY, summary);
		values.put(KEY_DESCRIPTION, description);
		return values;
	}

}