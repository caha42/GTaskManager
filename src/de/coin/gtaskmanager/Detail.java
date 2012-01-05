package de.coin.gtaskmanager;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import de.coin.gtaskmanager.R;
import de.coin.gtaskmanager.data.CalendarManager;
import de.coin.gtaskmanager.data.Constants;

/**
 * DetailView of an Task.
 * @author chaupt
 */
public class Detail extends Activity {

	private final static DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
	
	private long id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		id = getIntent().getLongExtra(Constants._ID, -1);
		Cursor cursor = getTask(id);
		
		String title = cursor.getString(cursor.getColumnIndexOrThrow(Constants.TITLE));
		((TextView) findViewById(R.id.title)).setText(title);

		String description = cursor.getString(cursor.getColumnIndexOrThrow(Constants.DESCRIPTION));
		((TextView) findViewById(R.id.description)).setText(description);
		
		Long duedate = cursor.getLong(cursor.getColumnIndexOrThrow(Constants.DUEDATE));
		if (duedate != Constants.NO_DUEDATE) {
			((TextView) findViewById(R.id.duedate_value)).setText(df.format(new Date(duedate)));
		} else {
			((TextView) findViewById(R.id.duedate_value)).setText("-");
		}
		
		boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.IS_DONE))==1;
		((CheckBox) findViewById(R.id.isdone)).setChecked(isDone);
	}

	/**
	 * Handle edit button.
	 * @param v
	 */
	public void edit(View v) {
		Intent intent = new Intent(v.getContext(), Edit.class);
		intent.putExtra(Constants._ID, id);
		startActivity(intent);
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
}
