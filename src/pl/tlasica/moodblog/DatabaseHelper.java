package pl.tlasica.moodblog;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static DatabaseHelper	singleton;
    
    private static final String[] columns = {
		Database.Entry.COLUMN_NAME_TSTAMP,
		Database.Entry.COLUMN_NAME_MOOD,
		Database.Entry.COLUMN_NAME_MESSAGE
    };
    
    public static DatabaseHelper create(Context context) {
    	if (singleton == null) {
    		singleton = new DatabaseHelper( context ); 
    	}
    	return singleton;
    }
    
    public static DatabaseHelper getInstance() {
    	return singleton;
    }
    
    private DatabaseHelper(Context context) {
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
    	Cursor cur = db.query(Database.Entry.TABLE_NAME, projection, null, null, null, null, sort, limit);
    	MoodEntry res = null;
    	if (cur.moveToFirst()) {
    		res = cursorToEntry(cur);
    	}
    	cur.close();
    	return res;
    }
    
    public Cursor fetchAllEntries() {
    	SQLiteDatabase db = getReadableDatabase();
    	Cursor cur = db.rawQuery("select rowid _id, * from entry order by tstamp desc", null);
    	if (cur != null) {
    		   cur.moveToFirst();
    	}
    	return cur;
    }
    
    //TODO: uwzględnić filtr po numDays
    public Map<Mood, Long> fetchStatistics(int numDays) {
    	Map<Mood,Long> res = new HashMap<Mood,Long>();

    	SQLiteDatabase db = getReadableDatabase();
    	Cursor cur = db.rawQuery("select mood, count(1) from entry group by mood", null); 

    	cur.moveToFirst();
    	do {
    		String mood = cur.getString( 0 );
    		Long count = cur.getLong( 1 );
    		res.put(Mood.fromString(mood), count);
    	} while( cur.moveToNext() );
    	
    	return res;
    }
    
        
    public MoodEntry cursorToEntry(Cursor cur) {
		long dt = cur.getLong( 0 );
		String mood = cur.getString( 1 );
		String message = cur.getString( 2 );
		return MoodEntry.create(mood, dt, message);    	
    }
}
