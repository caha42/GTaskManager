package de.coin.gtaskmanager;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import de.coin.gtaskmanager.data.CalendarManager;
import de.coin.gtaskmanager.data.Constants;

public class WidgetProvider extends AppWidgetProvider {

	private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i=0; i<N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Get the layout for the App Widget and attach an on-click listener
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			StringBuilder tasks = new StringBuilder();
			
			Cursor cursor = CalendarManager.getUnsolvedTasks(context, false);
			
			int j = 0;
			while (cursor.moveToNext() && j < 3) {
				long duedate = cursor.getLong(cursor.getColumnIndexOrThrow(Constants.DUEDATE));
				if (duedate != Constants.NO_DUEDATE) {
					tasks.append(df.format(new Date(duedate)));
					tasks.append(" ");
				}
				tasks.append(cursor.getString(cursor.getColumnIndex(Constants.TITLE)));
				tasks.append("\n");
				j++;
			}
			cursor.close();
			views.setTextViewText(R.id.widget_text, tasks);
			
			// Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, GTaskManager.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
			
			// Tell the AppWidgetManager to perform an update on the current App Widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}
