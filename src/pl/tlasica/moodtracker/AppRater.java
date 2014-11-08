package pl.tlasica.moodtracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tomek on 18.04.14.
 * http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
 */
public class AppRater {
    private final static String APP_PACKAGE = "pl.tlasica.moodtracker";
    private final static String PREF_FILE_NAME = "pl.tlasica.moodtracker.apprater";

    private final static int DAYS_UNTIL_PROMPT = 0;
    private final static int LAUNCHES_UNTIL_PROMPT = 6;


    private boolean launchAlways = false;
    private final Context mContext;
    private final SharedPreferences prefs;

    public AppRater(Context context) {
        this.mContext = context;
        this.prefs = mContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

    }

    public void onAppStart() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        long launchCount = incLaunchCount();
        long dateFirstLaunch = getFirstLaunchOrStoreIt();

        // Wait at least n days before opening
        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            long millisUntilPromt = DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000;
            if (System.currentTimeMillis() >= dateFirstLaunch + millisUntilPromt) {
                showRateDialog();
            }
        }
    }

    private long incLaunchCount() {
        long count = prefs.getLong("launch_count", 0) + 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("launch_count", count);
        editor.commit();
        Log.d("AppRater", "launch count:" + count);
        return count;
    }

    private long getFirstLaunchOrStoreIt() {
        Long dateFirstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (dateFirstLaunch == 0) {
            SharedPreferences.Editor editor = prefs.edit();
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", dateFirstLaunch);
            editor.commit();
            Log.d("AppRater", "first launch:" + dateFirstLaunch);
        } else {
            Log.d("AppRater", "launched:" + dateFirstLaunch);
        }
        return dateFirstLaunch;
    }

    public void showRateDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle( string(R.string.apprater_title) );

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(mContext);
        tv.setText( string(R.string.apprater_msg) );
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);

        Button bRate = new Button(mContext);
        bRate.setText(R.string.apprater_button_ok);
        bRate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                storeDoNotShowAgain();
                Uri uri = Uri.parse("market://details?id=" + APP_PACKAGE);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                dialog.dismiss();
            }
        });
        ll.addView(bRate);

        Button bLater = new Button(mContext);
        bLater.setText(string(R.string.apprater_button_later));
        bLater.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
                dialog.dismiss();
            }
        });
        ll.addView(bLater);

        Button b3 = new Button(mContext);
        b3.setText(string(R.string.apprater_button_no));
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                storeDoNotShowAgain();
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();
    }

    private void storeDoNotShowAgain() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dontshowagain", true);
        editor.commit();
    }

    private void reset() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("launch_count", 0);
        editor.putLong("date_firstlaunch", System.currentTimeMillis());
        editor.commit();
    }

    private String string(int rId) {
        return mContext.getResources().getString(rId);
    }

}
// see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater