package de.coin.gtaskmanager;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.coin.gtaskmanager.data.CalendarManager;
import de.coin.gtaskmanager.data.Constants;

public class Edit extends Activity {

	private final static DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
	static final int DATE_DIALOG_ID = 0;
	
	private long id;
	private Date duedate = new Date();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		
		id = getIntent().getLongExtra(Constants._ID, -1);
		if (id != -1) {
			loadDataIntoFields(getTask(id));
		} else {
			((Button) findViewById(R.id.duedate_value)).setText(df.format(duedate));
		}
	}
	
	public void toggleDuedateCheckbox(View v) {
	}
	
	public void showDatePickerDialog(View v) {
		showDialog(DATE_DIALOG_ID);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this, mDateSetListener, duedate.getYear()+1900, duedate.getMonth(), duedate.getDate());
	    }
	    return null;
	}
	
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			duedate = new Date(year-1900, monthOfYear, dayOfMonth);
			updateDisplay();
		}
	};
	
	private void updateDisplay() {
		((Button) findViewById(R.id.duedate_value)).setText(df.format(duedate));		
	}
	
	/**
	 * Handle save button. Save task and return to list view.
	 * @param v
	 */
	public void save(View v) {
		String title = ((EditText) findViewById(R.id.title_value)).getText().toString();
		// Check if Title is set
		if (title == null || title.length() == 0) {
			Toast.makeText(v.getContext(), getResources().getString(R.string.title_missing), 10000).show();
		} 
		else {
			// extract data from form
			String description = ((EditText) findViewById(R.id.description_value)).getText().toString();
			boolean isDuedate = ((CheckBox) findViewById(R.id.isduedate_value)).isChecked();
			boolean isDone = ((CheckBox) findViewById(R.id.isdone_value)).isChecked();
		
			// open db connection and insert data
			ContentValues values = new ContentValues();
			values.put(Constants.TITLE, title);
			values.put(Constants.DESCRIPTION, description);
			values.put(Constants.DUEDATE, (!isDuedate ? duedate.getTime() : Constants.NO_DUEDATE));
			values.put(Constants.IS_DONE, isDone);
			
			// insert or update data, depending if an id already exists
			if (id == -1) {
				CalendarManager.saveTask(this, values);
			} else {
				CalendarManager.updateTask(this, id, values);
			}
			
			// change to list view
			exit(v);
		}
	}
	
	/**
	 * Handle exit button. Return to list view.
	 * @param v
	 */
	public void exit(View v) {
		Intent intent = new Intent(v.getContext(), GTaskManager.class);
		startActivity(intent);
	}
	
	/**
	 * Handle delete button. Delete task from db.
	 * @param v
	 */
	public void delete(View v) {
		if (id != -1) {
			CalendarManager.deleteTask(this, id);
		}
		exit(v);
	}
	
	/**
	 * Load task with id <id> from db.
	 * @param id
	 * @return
	 */
	private Cursor getTask(long id) {
		Cursor cursor = CalendarManager.getTask(this, id);
		startManagingCursor(cursor);
		cursor.moveToFirst();
		return cursor;
	}

	/**
	 * Fill the fields of the form with data our of cursor.
	 * @param cursor
	 */
	private void loadDataIntoFields(Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndexOrThrow(Constants.TITLE));
		String description = cursor.getString(cursor.getColumnIndexOrThrow(Constants.DESCRIPTION));
		Long duedateTime = cursor.getLong(cursor.getColumnIndexOrThrow(Constants.DUEDATE));
		boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.IS_DONE))==1;
		
		if (title != null) {
			((TextView) findViewById(R.id.title_value)).setText(title);
		}
		if (description != null) {
			((TextView) findViewById(R.id.description_value)).setText(description);
		}
		((CheckBox) findViewById(R.id.isduedate_value)).setChecked(duedateTime == 0);
		if (duedateTime != Constants.NO_DUEDATE) {
			duedate = new Date(duedateTime);
		}
		((Button) findViewById(R.id.duedate_value)).setText(df.format(duedate));
		((CheckBox) findViewById(R.id.isdone_value)).setChecked(isDone);
	}
}
