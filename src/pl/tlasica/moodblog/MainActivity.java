package pl.tlasica.moodblog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String HAPPY ="HAPPY"; 
	public static final String NEUTRAL ="NEUTRAL"; 
	public static final String SAD ="SAD"; 
	public static final String ANGRY ="ANGRY"; 
	
	TextView			mLastStatus;
	TextView			mLastMessage;	
	EditText			mMessageEdit;
	
	Map<String,String>	faceColors = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
	
		// Initialize faceColors
		faceColors.put(HAPPY, "#FFB32A");
		faceColors.put(NEUTRAL, "#43CF30");
		faceColors.put(SAD, "#35A4FF");
		faceColors.put(ANGRY, "#E43DC0");
		
		// Initialize controls
		mLastStatus = (TextView) findViewById(R.id.last_status);
		mLastMessage = (TextView) findViewById(R.id.last_message);
		mMessageEdit = (EditText) findViewById(R.id.edit_message);		
		// TODO: fill last update field 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void recordHappy(View view) {
		recordMood(view, HAPPY);
	}
	
	public void recordNeutral(View view) {
		recordMood(view, NEUTRAL);		
	}

	public void recordSad(View view) {
		recordMood(view, SAD);		
	}

	public void recordAngry(View view) {
		recordMood(view, ANGRY);		
	}
	
	private void recordMood(View view, String mood) {
		Date now = new Date();
		// Get information about the input
		String message = mMessageEdit.getText().toString();
		// Save into the database
		saveRecordToDatabase(now, mood, message);
		// Update Last status and description
		String statusStr = getTimestampString( now ) + " >> " + mood; 
		mLastStatus.setText( statusStr );
		mLastMessage.setText( (message!=null) ? message : "" );
		// Change last status color
		changeLastStatusBackground(mood);		
		// Clean Description Text
		mMessageEdit.setText("");	
	}
	
	private void changeLastStatusBackground(String mood) {
		View v = mLastStatus;
		int color = Color.parseColor( faceColors.get( mood ) );
		v.setBackgroundColor(color);
		v.invalidate();
	}

	private void saveRecordToDatabase(Date now, String mood, String message) {
		// TODO Auto-generated method stub
		
	}

	private String getTimestampString(Date dt) {
		String date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(dt);
		String time = android.text.format.DateFormat.getTimeFormat(getApplicationContext()).format(dt);		
		return date + " " + time;
	}
	
}
