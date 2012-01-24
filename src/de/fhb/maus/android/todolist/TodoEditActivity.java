package de.fhb.maus.android.todolist;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import de.fhb.maus.android.todolist.R;
import de.fhb.maus.android.todolist.database.TodoDatabaseAdapter;

public class TodoEditActivity extends Activity {

	// private DatePicker mDatePicker;
	private Spinner mCategory;
	private CheckBox mCheckBox;
	private EditText mTitleText;
	private EditText mBodyText;
	private Button confirmButton;
	private Long mRowId;
	private TodoDatabaseAdapter mDbHelper;
	private Calendar mCalendar;
	private boolean backButtonOverClicked = false;

	private TextView mTextViewDate, mTextViewTime;
	private int mYear, mMonth, mDay, mHour, mMinute;
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mDbHelper = new TodoDatabaseAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.todo_edit);
		// mDatePicker = (DatePicker) findViewById(R.id.datePicker);
		mCategory = (Spinner) findViewById(R.id.category);
		mTitleText = (EditText) findViewById(R.id.summary);
		mBodyText = (EditText) findViewById(R.id.description);
		mCheckBox = (CheckBox) findViewById(R.id.checkBox);
		confirmButton = (Button) findViewById(R.id.button_save);

		mTextViewDate = (TextView) findViewById(R.id.textViewDate);
		mTextViewTime = (TextView) findViewById(R.id.textViewTime);

		mRowId = null;
		mCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+01:00"));
		Bundle extras = getIntent().getExtras();
		mRowId = (bundle == null) ? null : (Long) bundle
				.getSerializable(TodoDatabaseAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(TodoDatabaseAdapter.KEY_ROWID);
		}

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}
		});
		mTextViewDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		mTextViewTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
		populateFields();
	}
	private void updateDisplay() {
		mTextViewDate.setText(new StringBuilder().append(mYear).append("-")
				.append(mMonth + 1).append("-").append(mDay).append(" "));
		mTextViewTime.setText(new StringBuilder().append(mHour).append(":")
				.append(mMinute).append(" "));
	}
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int month, int day) {
			mYear = year;
			mMonth = month;
			mDay = day;
			updateDisplay();
		}
	};
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hour, int minute) {
			mHour = hour;
			mMinute = minute;
			updateDisplay();
		}
	};
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DATE_DIALOG_ID :
				return new DatePickerDialog(this, mDateSetListener, mYear,
						mMonth, mDay);
			case TIME_DIALOG_ID :
				return new TimePickerDialog(this, mTimeSetListener, mHour,
						mMinute, false);
		}
		return null;
	}

	// When showing the Edit Screen for a ToDo
	private void populateFields() {
		if (mRowId != null) {
			Cursor todo = mDbHelper.fetchTodo(mRowId);
			startManagingCursor(todo);
			String category = todo.getString(todo
					.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_CATEGORY));

			for (int i = 0; i < mCategory.getCount(); i++) {
				String s = (String) mCategory.getItemAtPosition(i);
				Log.e(null, s + " " + category);
				if (s.equalsIgnoreCase(category)) {
					mCategory.setSelection(i);
				}
			}

			if (todo.getInt(todo
					.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_DONE)) == 1) {
				mCheckBox.setChecked(true);
			} else {
				mCheckBox.setChecked(false);
			}

			mYear = mCalendar.get(Calendar.YEAR);
			mMonth = mCalendar.get(Calendar.MONTH);
			mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
			mHour = mCalendar.get(Calendar.HOUR);
			mMinute = mCalendar.get(Calendar.MINUTE);

			// A year y is represented by the integer y - 1900.
			// A month is represented by an integer from 0 to 11; 0 is January,
			// 1 is February, and so forth; thus 11 is December.
			// A date (day of month) is represented by an integer from 1 to 31
			// in the usual manner.
			mCalendar.setTimeInMillis(Long.valueOf(todo.getString(todo
					.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_DATE))));
			// mDatePicker.updateDate(mCalendar.getTime().getYear() + 1900,
			// mCalendar.getTime().getMonth(), mCalendar.getTime()
			// .getDate());
			updateDisplay();

			// An hour is represented by an integer from 0 to 23. Thus, the hour
			// from midnight to 1 a.m. is hour 0, and the hour from noon to 1
			// p.m. is hour 12.
			// A minute is represented by an integer from 0 to 59 in the usual
			// manner.
			// A second is represented by an integer from 0 to 61;

			//
			// DateFormat formatter = new
			// SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
			//
			// // von MILLISEC zu TIMESTAMP
			// // Datenbank
			// DateFormat df = DateFormat.getTimeInstance();
			// df.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
			// String gmtTime = df.format(new Date());
			// //System.out.println(gmtTime);
			//

			mTitleText.setText(todo.getString(todo
					.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_SUMMARY)));
			mBodyText
					.setText(todo.getString(todo
							.getColumnIndexOrThrow(TodoDatabaseAdapter.KEY_DESCRIPTION)));
		}
	}
	// by resuming
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(TodoDatabaseAdapter.KEY_ROWID, mRowId);
	}

	// Just Save the Edit if not the back button was clicked
	@Override
	protected void onPause() {
		super.onPause();
		if (!backButtonOverClicked) {
			saveState();
		}
	}

	// Looking for the pressevent of the back Button
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO
		// http://developer.android.com/reference/android/app/Activity.html#onBackPressed%28%29
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Log.d(this.getClass().getName(), "back button pressed");
			backButtonOverClicked = true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String category = (String) mCategory.getSelectedItem();
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();
		boolean done = mCheckBox.isChecked();

		mCalendar.set(mYear, mMonth, mDay, mHour, mMinute, 0);
		String date = String.valueOf(mCalendar.getTime().getTime());

		Toast.makeText(
				this,
				getResources().getString(R.string.additionalTodo)
						+ " "
						+ summary
						+ " "
						+ getResources()
								.getString(R.string.additionalTodoSaved),
				Toast.LENGTH_LONG).show();

		if (mRowId == null) {
			long id = mDbHelper.createTodo(date, category, done, summary,
					description);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateTodo(mRowId, date, category, done, summary,
					description);
		}
	}
}