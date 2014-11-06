package pl.tlasica.moodtracker;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * Created by tomek on 06.11.14.
 */
public class SmartTextSizer {

    Activity activity;

    public SmartTextSizer(Activity a) {
        this.activity = a;
    }

    public void setTextSize(TextView view, int numCharsPerRow) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        // lets try to get them back a font size realtive to the pixel width of the screen
        final float WIDE = activity.getResources().getDisplayMetrics().widthPixels;
        float valueWide = (int)(WIDE / (float)numCharsPerRow / (dMetrics.scaledDensity));
        view.setTextSize(valueWide);
    }

}
