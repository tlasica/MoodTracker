package pl.tlasica.moodblog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int 		DATABASE_VERSION = 1;
    public static final String 	DATABASE_NAME = "MoodTracker.db";

    private static final String[] columns = {
		Database.Entry.COLUMN_NAME_TSTAMP,
		Database.Entry.COLUMN_NAME_MOOD,
		Database.Entry.COLUMN_NAME_MESSAGE
    };
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Database.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	// TODO: Nothing so far
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public void saveEntry(MoodEntry entry) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	// Create a map of values
    	ContentValues values = new ContentValues();
    	values.put(Database.Entry.COLUMN_NAME_TSTAMP, entry.tstamp.getTime());
    	values.put(Database.Entry.COLUMN_NAME_MOOD, entry.mood.toString());
    	if (entry.message != null) {
    		values.put(Database.Entry.COLUMN_NAME_MESSAGE, entry.message);
    	}
    	// Insert
    	db.insert(Database.Entry.TABLE_NAME, "null", values);
    }
    
    public long getNumEntries() {
    	SQLiteDatabase db = getReadableDatabase();
    	long rows = DatabaseUtils.queryNumEntries(db, Database.Entry.TABLE_NAME);
    	return rows;
    }
    
    private static final String tstampDescSortOrder = Database.Entry.COLUMN_NAME_TSTAMP + " DESC";
        
    public MoodEntry getLastEntry() {
    	SQLiteDatabase db = getReadableDatabase();
    	String[] projection = columns;
    	String sort = tstampDescSortOrder;
    	String limit = "1";
    	Cursor c = db.query(false,	Database.Entry.TABLE_NAME, projection, null, null, null, null, sort, limit);
    	MoodEntry res = null;
    	if (c.moveToFirst()) {
    		int dt = c.getInt( 0 );
    		String mood = c.getString( 1 );
    		String message = c.getString( 2 );
    		res = MoodEntry.create(mood, dt, message);
    	}
    	c.close();
    	return res;
    }
    
}
