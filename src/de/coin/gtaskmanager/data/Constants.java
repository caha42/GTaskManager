package de.coin.gtaskmanager.data;


public final class Constants {
	
	// Column names
	public static String _ID = "_id";
	public static String TITLE = "title";
	public static String DESCRIPTION = "description";
	public static String DUEDATE = "dtstart";
	public static String IS_DONE = "eventStatus";
	
	public static String CALENDAR_ID = "calendar_id";
	public static String NAME = "name";
	public static String DELETED = "deleted";

	public static String _COUNT = "_count";
	
	// Acess array
	public static String[] FROM = { _ID, TITLE, DESCRIPTION, DUEDATE, IS_DONE, DELETED };

	public static String ORDER_BY_DUEDATE_DESC = DUEDATE + " DESC";
	
	// Shared Preferences
	public static String PREFS_NOF_TASKS = "prefs_nof_tasks";
	public static String PREFS_NO_DATELESS_TASKS = "prefs_no_dateless_tasks";
	
	public static long NO_DUEDATE = 0;
}