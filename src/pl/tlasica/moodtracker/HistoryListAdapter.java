package pl.tlasica.moodtracker;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HistoryListAdapter extends SimpleCursorAdapter {

	TimeStampFormatter		mFormat;
	
	public HistoryListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	public void setTimeStampFormatter(TimeStampFormatter formatter) {
		mFormat = formatter;
	}
	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent) {  	 
	   //get reference to the row
	   final View row = super.getView(position, convertView, parent);
	   // get data
       Cursor c = this.getCursor();
       c.moveToPosition(position);
	   String moodStr = c.getString( c.getColumnIndex(Database.Entry.COLUMN_NAME_MOOD));
	   Mood mood = Mood.fromString( moodStr );
	   int rgb = Color.parseColor( mood.colorRGB() );
	   
	   TextView tv = (TextView) row.findViewById( R.id.listitem_tstamp );
	   tv.setTextColor( rgb );
	   tv = (TextView) row.findViewById( R.id.listitem_mood );
	   tv.setTextColor( rgb );
	   
	   return row;  
	  }


	// TODO: nie podoba mi się to rozwiązanie
	@Override
	public void setViewText(TextView v, String text) {
		String newText = text;
		if ( v.getId() == R.id.listitem_tstamp ) {
			Date dt = new Date();
			dt.setTime( Long.valueOf( text ) );
			newText = mFormat.format( dt );
		}
		super.setViewText(v, newText);
	}  

}
