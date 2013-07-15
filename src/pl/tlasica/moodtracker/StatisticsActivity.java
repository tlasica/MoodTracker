package pl.tlasica.moodtracker;

import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class StatisticsActivity extends Activity {

	private CategorySeries		mSeries;
	private DefaultRenderer 	mRenderer;	
	private GraphicalView 		mChartView;
	
	private TextView			mTextNumWeeks;
	
	
	 @Override
	  protected void onRestoreInstanceState(Bundle savedState) {
	    super.onRestoreInstanceState(savedState);
	    mSeries = (CategorySeries) savedState.getSerializable("current_series");
	    mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
	  }

	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putSerializable("current_series", mSeries);
	    outState.putSerializable("current_renderer", mRenderer);
	  }
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		
		setupRenderer();
		
		mTextNumWeeks = (TextView) findViewById(R.id.text_numweeks);
		
		SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar_numweeks);	        
		seekbar.setOnSeekBarChangeListener( 
				new OnSeekBarChangeListener() {
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
							//empty
						}
							
						public void onStartTrackingTouch(SeekBar seekBar)	{
							// empty
						}

						public void onStopTrackingTouch(SeekBar seekBar) {
							int numWeeks = seekBar.getProgress();							
							mTextNumWeeks.setText( String.valueOf(numWeeks));
							prepareDataSeries( numWeeks );
							mChartView.repaint();
						}
					});
		
		int startWithTwoWeeks = 2;
		seekbar.setProgress( startWithTwoWeeks );
		mSeries = new CategorySeries("");
		prepareDataSeries( startWithTwoWeeks );
		mTextNumWeeks.setText( String.valueOf(startWithTwoWeeks) );
	}

	private void prepareDataSeries(int numWeeks) {
		DatabaseHelper db = DatabaseHelper.getInstance( this.getApplicationContext() );
		int numDays = numWeeks * 7;
		Map<Mood,Long> stats = db.fetchStatistics(numDays);
		
		mSeries.clear();
		for(Mood m: Mood.values()) {
			Long count = stats.get(m);
			mSeries.add(m.toString(), (count!=null) ? count : 0L );
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	    if (mChartView == null) {
	    	// create and configure mChartView
	    	mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
	    	
	    	// add chart view to layout	    	
	    	LinearLayout layout = (LinearLayout) findViewById(R.id.layout_piechart);
	    	layout.addView( mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));	      
	    } 
	    else {
	    	mChartView.repaint();
	    }	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_statistics, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupRenderer()
	{
		mRenderer = new DefaultRenderer();
		
		for(Mood mood: Mood.values()) {
			int color = Color.parseColor( mood.colorRGB() );
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor(color);
			simpleRenderer.setDisplayChartValues(true);
			simpleRenderer.setChartValuesTextSize(16);
			simpleRenderer.setShowLegendItem( true );
			mRenderer.addSeriesRenderer(simpleRenderer);
		}
		
		mRenderer.setDisplayValues( true );
		mRenderer.setAntialiasing( true );
		mRenderer.setLabelsTextSize(22);
		mRenderer.setLabelsColor( Color.LTGRAY );
		mRenderer.setLegendTextSize(20);
		mRenderer.setMargins( new int[]{0,0,0,0} );
	}	
}
