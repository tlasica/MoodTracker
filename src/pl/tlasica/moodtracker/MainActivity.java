package pl.tlasica.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	TextView			lastRecordedMood;
	TextView			lastRecordedMessage;
	DatabaseHelper		dbHelper;
	TimeStampFormatter	dtFormat;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		initializeLayoutAttributes();	
		dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		dtFormat = TimeStampFormatter.create( getApplicationContext() );
		
		//for testing only
		//TestHelper.forTestCreateHistoryOfMood(dbHelper, 42);
	}

		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadLastEntryAndUpdateView();		
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
			case R.id.menu_history:
				showHistoryActivity();
				return true;
			case R.id.menu_stats:
				showStatisticsActivity();
				return true;
            case R.id.menu_history_chart:
                showHistoryChartActivity();
                return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
		
	private void recordMood(View view, Mood mood) {
		Intent intent = new Intent(this, ConfirmSaveActivity.class);
		intent.putExtra(ConfirmSaveActivity.PARAM_MOOD_STR, mood.toString());
		startActivity(intent);
	}

	public void showHistoryActivity() {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}
	
	public void showStatisticsActivity() {
		Intent intent = new Intent(this, StatisticsActivity.class);
		startActivity(intent);
	}

    public void showHistoryChartActivity() {
        HistoryChartGraph graph = new HistoryChartGraph();
        Intent intent = graph.getBarChartPerDayIntent(this);
        if (intent != null) {
            startActivity(intent);
        }
        else Toast.makeText(this, "No history. Cannot show chart :-(", Toast.LENGTH_LONG);
    }

	private void updateLastView(MoodEntry entry) {
		String dtStr = dtFormat.format( entry.tstamp );
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
	
}
