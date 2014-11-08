package pl.tlasica.moodtracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends Activity {
	
	TextView			lastRecordedMood;
	TextView			lastRecordedMessage;
	DatabaseHelper		dbHelper;
	TimeStampFormatter	dtFormat;
    AdView              adView;
    UiLifecycleHelper   uiHelper;
    BroadcastReceiver   mConnReceiver = null;
    boolean             isInternetConnected = false;
    MoodEntry           lastEntry = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		initializeLayoutAttributes();	
		dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		dtFormat = TimeStampFormatter.create( getApplicationContext() );

        int LINK_TEXT_SIZE = 20;
        int HEAD_TEXT_SIZE = 16;
        int NORM_TEXT_SIZE = 20;
        int SMALL_TEXT_SIZE = 24;

        SmartTextSizer sizer = new SmartTextSizer(this);
        // links
        sizer.setTextSize((TextView)findViewById(R.id.link_history), LINK_TEXT_SIZE);
        sizer.setTextSize((TextView)findViewById(R.id.link_statistics), LINK_TEXT_SIZE);
        sizer.setTextSize((TextView)findViewById(R.id.link_calendar), LINK_TEXT_SIZE);
        // labels
        sizer.setTextSize((TextView)findViewById(R.id.label_howdoyoufeel), HEAD_TEXT_SIZE);
        sizer.setTextSize((TextView)findViewById(R.id.label_click), SMALL_TEXT_SIZE);
        // last status
        sizer.setTextSize((TextView)findViewById(R.id.last_update_label), HEAD_TEXT_SIZE);
        sizer.setTextSize((TextView)findViewById(R.id.last_status), NORM_TEXT_SIZE);
        sizer.setTextSize((TextView)findViewById(R.id.last_message), SMALL_TEXT_SIZE);

        configureGooleAds();

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        new AppRater(this).onAppStart();

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
        uiHelper.onDestroy();
	}

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
        uiHelper.onPause();
        if (mConnReceiver != null) {
            this.unregisterReceiver(mConnReceiver);
            mConnReceiver = null;
        }
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
	protected void onResume() {
        adView.resume();
		super.onResume();
        uiHelper.onResume();
		loadLastEntryAndUpdateView();
        // register to see connectivity changes
        if (mConnReceiver == null) {
            mConnReceiver = createBroadcastReceiver();
            registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private ImageButton getShareOnFacebookButton() {
        return (ImageButton) findViewById(R.id.shareOnFacebookButton);
    }

	private void initializeLayoutAttributes() {
		lastRecordedMood = (TextView) findViewById(R.id.last_status);
		lastRecordedMessage = (TextView) findViewById(R.id.last_message);
	}
	
	private void loadLastEntryAndUpdateView() {
		MoodEntry entry = dbHelper.getLastEntry();
		if (entry != null) {
			updateLastView( entry );
            lastEntry = entry;
		}
        updateFacebookButtonVisibility();
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

    private BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connManager.getActiveNetworkInfo();
                if (netInfo!=null && netInfo.isConnected()) {
                    Log.d("NETWORK", "network is connected");
                    isInternetConnected = true;
                    // show facebook share button
                    //getShareOnFacebookButton().setVisibility(View.VISIBLE);
                }
                else {
                    Log.d("NETWORK", "network is disconnected");
                    isInternetConnected = false;
                    //getShareOnFacebookButton().setVisibility(View.GONE);
                }
            }
        };
    }

    private void  updateFacebookButtonVisibility() {
        if (lastEntry == null) getShareOnFacebookButton().setVisibility(View.INVISIBLE);
        else getShareOnFacebookButton().setVisibility(View.VISIBLE);
    }

    public void shareOnFacebook(View view) {
        if (isInternetConnected) {
            if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
                facebookShareWithFeedDialog();
            }
            else {
                Session.openActiveSession(this, true, new Session.StatusCallback() {
                    @Override
                    public void call(Session session, SessionState state, Exception exception) {
                        if (state == SessionState.OPENED) facebookShareWithFeedDialog();
                    }
                });
            }
        } else {
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT ).show();
        }
    }


    public void facebookShareWithFeedDialog() {
        if (lastEntry == null) {
            Log.e("OOOPS", "Fatal bug: lastEntry cannot be null when sharing via facebook!");
            return;
        }

        Bundle params = new Bundle();
        if (lastEntry.message != null)
            params.putString("name", lastEntry.message);
        else
            params.putString("name", "I feel " + lastEntry.mood.toString() );
        params.putString("link", "http://bit.ly/moodtracker");
        params.putString("picture", pictureUrl(this.lastEntry.mood));
        params.putString("description", "Recorded with Mood Tracker Android App on " + dtFormat.format( lastEntry.tstamp ));

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error == null) {
                            final String postId = values.getString("post_id");
                            if (postId != null) Log.d("FACEBOOK", "posted");
                        } else if (error instanceof FacebookOperationCanceledException) {
                            Log.i("FACEBOOK", "Publish cancelled");
                        } else {
                            Log.w("FACEBOOK", "Error posting story");
                        }
                    }

                })
                .build();
        feedDialog.show();
    }

    private String pictureUrl(Mood mood) {
        String baseUrl = "https://raw.githubusercontent.com/tlasica/MoodTracker/master/res/drawable-hdpi/";
        return baseUrl + mood.getImageFile();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("FB Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("FB Activity", "Success!");
            }
        });
    }//onActivityResult

}
