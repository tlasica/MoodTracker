package pl.tlasica.moodtracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmSaveActivity extends Activity {

	public final static String PARAM_MOOD_STR = "pl.tlasica.moodtracker.MOOD";

	Mood				moodToSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_save);

        // set text sizes
        SmartTextSizer sizer = new SmartTextSizer(this);
        sizer.setTextSize((TextView)findViewById(R.id.confirm_save_mood), 12);
        sizer.setTextSize((TextView)findViewById(R.id.label_suggest_description), 24);

		// get mood from intent
		Intent intent = getIntent();
		String moodToSaveStr = intent.getStringExtra(PARAM_MOOD_STR);
		moodToSave = Mood.fromString(moodToSaveStr);
		// update text with mood to save and change color
		TextView textViewMood = (TextView) findViewById(R.id.confirm_save_mood);
        String str = "I feel " + moodToSave.toString();
		textViewMood.setText(str);
		int color = Color.parseColor( moodToSave.colorRGB() );
		textViewMood.setTextColor(color);
		textViewMood.invalidate();
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_confirm_save, menu);
		return true;
	}
	
	public void onSave(View view) {
		EditText messageEdit = (EditText) findViewById(R.id.edit_message);
        String message = messageEdit.getText().toString();
        MoodEntry entry = MoodEntry.createNow(moodToSave, message);
        DatabaseHelper db = DatabaseHelper.getInstance( this.getApplicationContext() ); 
        db.saveEntry( entry );
        this.finish();
	}

}
