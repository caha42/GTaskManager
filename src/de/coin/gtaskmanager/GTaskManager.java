package de.coin.gtaskmanager;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import de.coin.gtaskmanager.adapter.CheckboxCursorAdapter;
import de.coin.gtaskmanager.data.CalendarManager;
import de.coin.gtaskmanager.data.Constants;

public class GTaskManager extends ListActivity  {
	
    /** 
     * Called when the activity is first created. 
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	/**
        	 * Open detail-page for clicked item. Hand over ID for access.
        	 */
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		CheckBox isDoneCheckBox = (CheckBox) view.findViewById(R.id.isdone);
        		isDoneCheckBox.setChecked(!isDoneCheckBox.isChecked());
				CalendarManager.updateIsDone(view.getContext(), id, isDoneCheckBox.isChecked());
				showTasks(getTasks());
        	}
        });
        registerForContextMenu(lv);
        
        showTasks(getTasks());
    }

    /**
	 * Initialize Context Menu.
	 * @param menu
	 * @param v
	 * @param menuInfo
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	/**
	 * Handle Context Menu selection.
	 * @param item
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		long id = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).id;
		switch (item.getItemId()) {
		case R.id.detail:
			Intent detail = new Intent(this, Detail.class);
			detail.putExtra(Constants._ID, id);
			startActivity(detail);
			return true;
		case R.id.edit:
			Intent edit = new Intent(this, Edit.class);
			edit.putExtra(Constants._ID, id);
			startActivity(edit);
			return true;
		case R.id.delete:
			CalendarManager.deleteTask(this, id);
			showTasks(getTasks());
			return true;
		}
		return false;
	}

	/**
	 * Initialize Options Menu.
	 * @param menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/**
	 * Handle Options Menu selection.
	 * @param item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			startActivity(new Intent(this, Edit.class));
			return true;
		case R.id.delete_done:
			CalendarManager.deleteResolvedTasks(this);
			showTasks(getTasks());
			return true;
		}
		
		return false;
	}

	/**
	 * @see android.app.ListActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		onCreate(null);
	}
    
	/**
	 * Get all tasks from db.
	 * @return
	 */
	private Cursor getTasks() {
		Cursor cursor = CalendarManager.getTasks(this);
		startManagingCursor(cursor);
		return cursor;
	}
	
	/**
	 * Show all tasks in ListView.
	 * @param cursor
	 */
	private void showTasks(Cursor cursor) {
		CheckboxCursorAdapter adapter = new CheckboxCursorAdapter(this, R.layout.item, cursor);
		setListAdapter(adapter);
	}
}