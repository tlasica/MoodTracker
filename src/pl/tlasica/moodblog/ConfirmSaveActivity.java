package pl.tlasica.moodblog;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ConfirmSaveActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_save);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_confirm_save, menu);
		return true;
	}

}
