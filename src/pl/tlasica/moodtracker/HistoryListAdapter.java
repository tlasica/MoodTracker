package pl.tlasica.moodtracker;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryListAdapter extends SimpleCursorAdapter {

	TimeStampFormatter		mFormat;
    SmartTextSizer          textSizer;
    Context                 mContext;
	
	public HistoryListAdapter(SmartTextSizer sizer, Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
        mContext = context;
        textSizer = sizer;
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
        final Long entryId = c.getLong(c.getColumnIndex(Database.Entry._ID));
	    Mood mood = Mood.fromString( moodStr );
	    int rgb = Color.parseColor( mood.colorRGB() );

	    TextView tv = (TextView) row.findViewById( R.id.listitem_tstamp );
	    tv.setTextColor( rgb );
        textSizer.setTextSize(tv, 20);

	    tv = (TextView) row.findViewById( R.id.listitem_mood );
	    tv.setTextColor( rgb );
        textSizer.setTextSize(tv, 20);

        ImageView image = (ImageView) row.findViewById(R.id.listitem_face);
        if (image != null) {
            image.setImageResource(mood.getImage());
        }

        View trash = row.findViewById(R.id.listitem_trash);
        if (trash != null) {
            trash.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder bld = new AlertDialog.Builder(mContext);
                    bld.setTitle("Confirmation");
                    bld.setMessage("Please confirm removal")
                            .setCancelable(true)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    removeEntryById(entryId);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog dialog = bld.create();
                    dialog.show();
                }
            });
        }

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


    public void removeEntryById(Long id) {
        Log.d("REMOVE", "id:" + id);
        try {
            DatabaseHelper db = DatabaseHelper.getInstance(mContext.getApplicationContext());
            db.removeEntry( id );
            this.changeCursor(db.fetchAllEntries());
        } catch (Exception e) {
            Toast.makeText(mContext, "Something went wrong...", Toast.LENGTH_SHORT).show();
            Log.e("REMOVE", e.getMessage());
        }
    }
}
