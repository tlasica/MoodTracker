package pl.tlasica.moodtracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends Activity {
	
	TextView			lastRecordedMood;
	TextView			lastRecordedMessage;
	DatabaseHelper		dbHelper;
	TimeStampFormatter	dtFormat;
    private AdView      adView;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		initializeLayoutAttributes();	
		dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		dtFormat = TimeStampFormatter.create( getApplicationContext() );

        int LINK_TEXT_SIZE = 20;
        int HEAD_TEXT_SIZE = 20;
        int NORM_TEXT_SIZE = 28;
        int SMALL_TEXT_SIZE = 34;

        // links
        setSmartSize((TextView)findViewById(R.id.link_history), LINK_TEXT_SIZE);
        setSmartSize((TextView)findViewById(R.id.link_statistics), LINK_TEXT_SIZE);
        setSmartSize((TextView)findViewById(R.id.link_calendar), LINK_TEXT_SIZE);
        // labels
        setSmartSize((TextView)findViewById(R.id.label_howdoyoufeel), HEAD_TEXT_SIZE);
        setSmartSize((TextView)findViewById(R.id.label_click), SMALL_TEXT_SIZE);
        // last status
        setSmartSize((TextView)findViewById(R.id.last_update_label), HEAD_TEXT_SIZE);
        setSmartSize((TextView)findViewById(R.id.last_status), NORM_TEXT_SIZE);
        setSmartSize((TextView)findViewById(R.id.last_message), SMALL_TEXT_SIZE);

        configureGooleAds();
		//for testing only
		//TestHelper.forTestCreateHistoryOfMood(dbHelper, 42);
	}

    private void configureGooleAds() {
        // configure google admob
        adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A9A32839D5C3A3E567C5D00C21437288")
                .build();
        adView.loadAd(adRequest);
    }


    @Override
	protected void onDestroy() {
        adView.destroy();
		super.onDestroy();
		dbHelper.close();
	}

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
	protected void onResume() {
        adView.resume();
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

	public void showHistory(View view) {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}
	
	public void showStatistics(View view) {
		Intent intent = new Intent(this, StatisticsActivity.class);
		startActivity(intent);
	}

    public void showCalendar(View view) {
        HistoryChartGraph graph = new HistoryChartGraph();
        Intent intent = graph.getBarChartPerDayIntent(this);
        if (intent != null) {
            startActivity(intent);
        }
        else Toast.makeText(this, getString(R.string.message_no_history_no_chart), Toast.LENGTH_LONG).show();
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

    private void setSmartSize(TextView view, int numCharsPerRow) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        // lets try to get them back a font size realtive to the pixel width of the screen
        final float WIDE = getResources().getDisplayMetrics().widthPixels;
        float valueWide = (int)(WIDE / (float)numCharsPerRow / (dMetrics.scaledDensity));
        view.setTextSize(valueWide);
    }
}
