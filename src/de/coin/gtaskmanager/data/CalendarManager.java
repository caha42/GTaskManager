package de.coin.gtaskmanager.data;

import de.coin.gtaskmanager.WidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class CalendarManager {

	private static final int ONE_DAY = 1000 * 60 * 60 * 24;
	private static final String EVENTS = "events";
	private static final String CALENDARS = "calendars";
	
	private static final String TASK_CAL_NAME = "Tasks";
	private static long TASK_CAL_ID = -1;
	
	/**
	 * Load task with id <id> from db.
	 * @param context
	 * @param id
	 * @return
	 */
	public static Cursor getTask(Context context, long id) {
		return getCursor(context, Constants.FROM, null, null, null, EVENTS + "/" + id);
	}

	/**
	 * Get all tasks from tasks calendar.
	 * @param context
	 * @return
	 */
	public static Cursor getTasks(Context context) {
		// get ID for calendar "Tasks"
		if (TASK_CAL_ID == -1) {
			TASK_CAL_ID = getTaskCalendarId(context);
		}		
		// load events from calendar
		return getCursor(context, Constants.FROM, Constants.CALENDAR_ID+"=? AND "+Constants.DELETED+"=?", new String[] {String.valueOf(TASK_CAL_ID), "0"}, null, EVENTS);
	}

	/**
	 * Load next <n> unresolved tasks from db.
	 * @param context
	 * @param onlyWithDuedate 
	 * @return
	 */
	public static Cursor getUnsolvedTasks(Context context, boolean onlyWithDuedate) {
		// get ID for calendar "Tasks"
		if (TASK_CAL_ID == -1) {
			TASK_CAL_ID = getTaskCalendarId(context);
		}
		// load events from calendar
		Cursor cursor;
		if (onlyWithDuedate) {
			cursor = getCursor(context, Constants.FROM, Constants.CALENDAR_ID+"=? AND "+Constants.IS_DONE+"=? AND "+Constants.DELETED+"=? AND NOT "+Constants.DUEDATE+"=?", new String[] {String.valueOf(TASK_CAL_ID), "0", "0", Constants.NO_DUEDATE+""}, Constants.ORDER_BY_DUEDATE_DESC, EVENTS);        
		} else {
			cursor = getCursor(context, Constants.FROM, Constants.CALENDAR_ID+"=? AND "+Constants.IS_DONE+"=? AND "+Constants.DELETED+"=?", new String[] {String.valueOf(TASK_CAL_ID), "0", "0"}, Constants.ORDER_BY_DUEDATE_DESC, EVENTS);        
		}
		return cursor;
	}
	
	/**
	 * Save task in calendar.
	 * @param context
	 * @param values
	 */
	public static void saveTask(Context context, ContentValues values) {
		// get ID for calendar "Tasks"
		if (TASK_CAL_ID == -1) {
			TASK_CAL_ID = getTaskCalendarId(context);
		}
		values.put("calendar_id", TASK_CAL_ID);
		// add one day if not 0 - google calendar bug
		adjustDuedate(values);
		values.put("allDay", 1); // 0 for false, 1 for true
		values.put("eventStatus", 0);
		values.put("deleted", 0);
		values.put("visibility", 0);
		values.put("transparency", 0);
		values.put("hasAlarm", 0); // 0 for false, 1 for true
	
		Uri eventsUri = Uri.parse(getCalendarUriBase(context)+"events");
		context.getContentResolver().insert(eventsUri, values);
		
		updateWidget(context);
	}

	/**
	 * Update task in calendar.
	 * @param context
	 * @param id
	 * @param values
	 */
	public static void updateTask(Context context, long id, ContentValues values) {
		adjustDuedate(values);
		
		Uri eventsUri = Uri.parse(getCalendarUriBase(context)+"events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, id);
		context.getContentResolver().update(eventUri, values, Constants._ID+"=?", new String[] {String.valueOf(id)});

		updateWidget(context);
	}

	/**
	 * Update isDone flag from task.
	 * @param context
	 * @param id
	 * @param checked
	 */
	public static void updateIsDone(Context context, long id, boolean checked) {
		ContentValues values = new ContentValues();
		values.put(Constants.IS_DONE, (checked ? 1 : 0));
		updateTask(context, id, values);
		
		updateWidget(context);
	}

	/**
	 * Delete task from calendar.
	 * @param context
	 * @param id
	 */
	public static void deleteTask(Context context, long id) {
        Uri eventsUri = Uri.parse(getCalendarUriBase(context)+"events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, id);
        context.getContentResolver().delete(eventUri, null, null);
        
        updateWidget(context);
	}

	/**
	 * Delete all resolved tasks from calendar.
	 * @param context
	 */
	public static void deleteResolvedTasks(Context context) {
        Uri eventsUri = Uri.parse(getCalendarUriBase(context)+"events");
		context.getContentResolver().delete(eventsUri, Constants.IS_DONE+"=?", new String[] {"1"});
		updateWidget(context);
	}
	
    /**
     * Get managed cursor for calendar by path.
     * @param context
     * @param projection
     * @param selection
     * @param path
     * @return
     */
    private static Cursor getCursor(Context context, String[] projection, String selection, String[] selectionArgs, String sortOrder, String path) {
        Uri calendars = Uri.parse(getCalendarUriBase(context) + path);
        return context.getContentResolver().query(calendars, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the calendar base Uri
     * @param context
     * @raturn
     */
    private static String getCalendarUriBase(Context context) {
        String calendarUriBase = null;
        
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://calendar/calendars"), null, null, null, null);
        if (cursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
        	cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);
            if (cursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }

	private static long getTaskCalendarId(Context context) {
		Cursor cursor = getCursor(context, new String[] { Constants._ID, Constants.NAME }, Constants.NAME+"=?", new String[] {TASK_CAL_NAME}, null, CALENDARS);
		cursor.moveToFirst();
		return cursor.getLong(cursor.getColumnIndex(Constants._ID));
	}

	/**
	 * Due to some reason the duedate has to be raised one day.
	 * @param values
	 */
	private static void adjustDuedate(ContentValues values) {
		Long duedate = values.getAsLong("dtstart");
		if (duedate != null) {
			if (duedate != 0) {
				duedate += ONE_DAY;
			}
			values.put("dtstart", duedate);
			values.put("dtend", duedate+ONE_DAY);
		}
	}

	private static void updateWidget(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
	    ComponentName componentName = new ComponentName(context.getApplicationContext(), WidgetProvider.class);
	    int[] ids = appWidgetManager.getAppWidgetIds(componentName);
	    
	    Intent update_widget = new Intent();
	    update_widget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
	    update_widget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	    context.getApplicationContext().sendBroadcast(update_widget);
	}
}
