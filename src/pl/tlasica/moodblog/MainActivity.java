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
	
	TextView			lastRecordedMood;
	TextView			lastRecordedMessage;
	DatabaseHelper		dbHelper;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		initializeLayoutAttributes();	
		dbHelper = new DatabaseHelper(this);
		loadLastEntryAndUpdateView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}
	
	private void initializeLayoutAttributes() {
		lastRecordedMood = (TextView) findViewById(R.id.last_status);
		lastRecordedMessage = (TextView) findViewById(R.id.last_message);
	}
	
	private void loadLastEntryAndUpdateView() {
		MoodEntry entry = dbHelper.getLastEntry();
		if (entry != null) {
			updateLastView( entry );
		}		
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
	
	public void showStatistics(View view) {
		///
	}
	
	//TODO: obsługa błędów?
	//TODO: start new activity !!!
	private void recordMood(View view, Mood mood) {
		/*
		String message = messageEditBox.getText().toString();
		MoodEntry entry = MoodEntry.createNow(mood, message);
		dbHelper.saveEntry( entry );
		updateLastView( entry );
		*/
	}

	private void updateLastView(MoodEntry entry) {
		String dtStr = getTimestampString( entry.tstamp );
		String statusStr = String.format("%s on %s", entry.mood, dtStr);
		lastRecordedMood.setText( statusStr );
		lastRecordedMessage.setText( (entry.message!=null) ? entry.message : "" );
		changeLastStatusColors(entry.mood);
	}
	
	private void changeLastStatusColors(Mood mood) {
		int color = Color.parseColor( mood.colorRGB() );
		lastRecordedMood.setTextColor(color);
		lastRecordedMood.invalidate();
	}

	//TODO: create separate class
	private String getTimestampString(Date dt) {
		String date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(dt);
		String time = android.text.format.DateFormat.getTimeFormat(getApplicationContext()).format(dt);		
		return date + " " + time;
	}
	
}
