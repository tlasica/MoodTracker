package pl.tlasica.moodtracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class HistoryActivity extends Activity {

	HistoryListAdapter		mAdapter;
	Cursor					mCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		ListView listview = (ListView)findViewById(R.id.history_listview);
		
		String[] fromColumns = {Database.Entry.COLUMN_NAME_TSTAMP, Database.Entry.COLUMN_NAME_MOOD, Database.Entry.COLUMN_NAME_MESSAGE};
		int[] toViews = {R.id.listitem_tstamp, R.id.listitem_mood, R.id.listitem_message};
				
		DatabaseHelper db = DatabaseHelper.getInstance( this.getApplicationContext() );		
		mCursor = db.fetchAllEntries();
		
		mAdapter = new HistoryListAdapter(this, R.layout.history_list_item, mCursor, fromColumns, toViews, 0);
		mAdapter.setTimeStampFormatter( TimeStampFormatter.create( getApplicationContext() ));
		listview.setAdapter( mAdapter );		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_history, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCursor.close();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
