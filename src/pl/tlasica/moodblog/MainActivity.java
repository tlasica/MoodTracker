package pl.tlasica.moodblog;

import java.util.Date;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

// TODO: warto skorzystać, bo można zrobić automatycznie skalujące się przyciski
// http://www.androidhive.info/2012/02/android-gridview-layout-tutorial/

public class MainActivity extends Activity {
	
	TextView			lastUpdateLabel;
	TextView			lastStatus;
	TextView			lastMessage;	
	EditText			messageEditBox;
	DatabaseHelper		dbHelper;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		initializeLayoutAttributes();	
		dbHelper = new DatabaseHelper(this);
		updateNumberOfRecords();
		loadLastEntryAndUpdateView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}
	
	private void initializeLayoutAttributes() {
		lastUpdateLabel = (TextView) findViewById(R.id.last_update_label);
		lastStatus = (TextView) findViewById(R.id.last_status);
		lastMessage = (TextView) findViewById(R.id.last_message);
		messageEditBox = (EditText) findViewById(R.id.edit_message);		
	}
	
	private void loadLastEntryAndUpdateView() {
		MoodEntry entry = dbHelper.getLastEntry();
		if (entry != null) {
			updateLastView( entry );
		}		
	}

	private void updateNumberOfRecords() {
		long rows = dbHelper.getNumEntries();
		String str = String.format("Last recorded mood (%d recorded):", rows );
		lastUpdateLabel.setText( str );
		lastUpdateLabel.invalidate();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void recordHappy(View view) {
		recordMood(view, Mood.HAPPY);
	}
	
	public void recordNeutral(View view) {
		recordMood(view, Mood.NEUTRAL);		
	}

	public void recordSad(View view) {
		recordMood(view, Mood.SAD);		
	}

	public void recordAngry(View view) {
		recordMood(view, Mood.ANGRY);		
	}
	
	//TODO: obsługa błędów?
	private void recordMood(View view, Mood mood) {
		String message = messageEditBox.getText().toString();
		MoodEntry entry = MoodEntry.createNow(mood, message);
		dbHelper.saveEntry( entry );
		updateLastView( entry );
		messageEditBox.setText("");
		updateNumberOfRecords();
	}

	private void updateLastView(MoodEntry entry) {
		String dtStr = getTimestampString( entry.tstamp );
		String statusStr = String.format("%s on %s", entry.mood, dtStr);
		lastStatus.setText( statusStr );
		lastMessage.setText( (entry.message!=null) ? entry.message : "" );
		changeLastStatusColors(entry.mood);
	}
	
	private void changeLastStatusColors(Mood mood) {
		int color = Color.parseColor( mood.colorRGB() );
		lastStatus.setTextColor(color);
		lastStatus.invalidate();
	}

	//TODO: create separate class
	private String getTimestampString(Date dt) {
		String date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(dt);
		String time = android.text.format.DateFormat.getTimeFormat(getApplicationContext()).format(dt);		
		return date + " " + time;
	}
	
}
