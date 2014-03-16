package pl.tlasica.moodtracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;

public class HistoryActivity extends Activity {

	HistoryListAdapter		mAdapter;
	Cursor					mCursor;
    int                     selectedItem = -1;

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

        // set action bar to show-up
        listview.setOnItemClickListener( onListItemClickListener() );
        this.registerForContextMenu( listview );
    }

    private AdapterView.OnItemClickListener onListItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                view.showContextMenu();
            }
        };
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d("MAIN", "onCreateContextMenu()");
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_item_selected, menu);
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

    public void onMenuRemoveItem(MenuItem item) {
        Log.d("REMOVE", "selected:" + selectedItem);
        if (selectedItem >= 0) {
            Long id = mAdapter.getItemId( selectedItem );
            Log.d("REMOVE", "id:" + id);
            // remove from database
            try {
                DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
                db.removeEntry( id );
                // update content
                mAdapter.changeCursor( db.fetchAllEntries() );
//                mAdapter.notifyDataSetChanged();
                // clear selection
                selectedItem = -1;
            }
            catch (Exception e) {
                Log.e("REMOVE", e.getMessage());

            }
        }
    }
}
