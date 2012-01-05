package de.coin.gtaskmanager;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;
import android.widget.RemoteViews;

public class WidgetConfig extends PreferenceActivity {

//	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		addPreferencesFromResource(R.xml.widget_prefs);
//		
//		EditTextPreference etp = (EditTextPreference) findPreference(getResources().getString(R.string.nof_tasks));
//        EditText et = (EditText) etp.getEditText();
//        et.setKeyListener(DigitsKeyListener.getInstance());
//		
//		Intent intent = getIntent();
//		Bundle extras = intent.getExtras();
//		if (extras != null) {
//		    mAppWidgetId = extras.getInt(
//		            AppWidgetManager.EXTRA_APPWIDGET_ID, 
//		            AppWidgetManager.INVALID_APPWIDGET_ID);
//		}
//		
//		 // If they gave us an intent without the widget id, just bail.
//        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
//            finish();
//        }
	}

//	@Override
//	protected void onPause() {
//		updateWidget();
//		closeConfig();
//		super.onPause();
//	}
//	
//	private void closeConfig() {
//		Intent resultValue = new Intent();
//		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
//		setResult(RESULT_OK, resultValue);
//		finish();
//	}
//
//	private void updateWidget() {
//		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
//		appWidgetManager.updateAppWidget(mAppWidgetId, views);
//	}
//	
}
