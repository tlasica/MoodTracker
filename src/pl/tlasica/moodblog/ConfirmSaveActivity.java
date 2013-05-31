package pl.tlasica.moodblog;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmSaveActivity extends Activity {

	public final static String PARAM_MOOD_STR = "pl.tlasica.moodblog.MOOD";

	Mood				moodToSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_save);
		// get mood from intent
		Intent intent = getIntent();
		String moodToSaveStr = intent.getStringExtra(PARAM_MOOD_STR);
		moodToSave = Mood.fromString(moodToSaveStr);
		// update text with mood to save and change color
		TextView textViewMood = (TextView) findViewById(R.id.confirm_save_mood);
		textViewMood.setText(moodToSave.toString());
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
        DatabaseHelper db = DatabaseHelper.getInstance(); 
        db.saveEntry( entry );
        this.finish();
	}

}
