package de.coin.gtaskmanager.adapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import de.coin.gtaskmanager.R;
import de.coin.gtaskmanager.data.Constants;

public class CheckboxCursorAdapter extends ResourceCursorAdapter {

	private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
	
	public CheckboxCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndexOrThrow(Constants.TITLE));
		((TextView) view.findViewById(R.id.title)).setText(title);

		String description = cursor.getString(cursor.getColumnIndexOrThrow(Constants.DESCRIPTION));
		((TextView) view.findViewById(R.id.description)).setText(description);
		
		boolean isDone = cursor.getInt(cursor.getColumnIndexOrThrow(Constants.IS_DONE))==1;
		((CheckBox) view.findViewById(R.id.isdone)).setChecked(isDone);
		
		long duedate = cursor.getLong(cursor.getColumnIndexOrThrow(Constants.DUEDATE));
		
		Log.d("GCAL", title + ": " + duedate);
		TextView dateView = (TextView) view.findViewById(R.id.daysleft);
		if (duedate != Constants.NO_DUEDATE) {
			Date date = new Date(duedate);
			if ((DateUtils.isToday(duedate) || date.before(new Date())) && !isDone) {
				dateView.setTextColor(Color.RED);
			} else {
				dateView.setTextColor(Color.LTGRAY);
			}
			dateView.setText(df.format(date));
		} else {
			dateView.setText("");
		}
	}


}
