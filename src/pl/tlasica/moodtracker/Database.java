package pl.tlasica.moodtracker;

import android.provider.BaseColumns;

/**
 * Contract class for application database
 * @author tomek
 *
 */
public class Database {

	public static abstract class Entry implements BaseColumns {
		
		public static final String TABLE_NAME = "entry";
	    public static final String COLUMN_NAME_MOOD = "mood";
	    public static final String COLUMN_NAME_TSTAMP = "tstamp";
	    public static final String COLUMN_NAME_MESSAGE = "message";
	    public static final String COLUMN_NAME_EXTRA = "extra";
	    
	}
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String DATE_TYPE = " INTEGER";	// for sqlite
	private static final String COMMA_SEP = ",";
	private static final String NOT_NULL = " NOT NULL";
	
	static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + Entry.TABLE_NAME + " (" +
	    Entry._ID + " INTEGER PRIMARY KEY," +
	    Entry.COLUMN_NAME_MOOD + TEXT_TYPE + NOT_NULL + COMMA_SEP +
	    Entry.COLUMN_NAME_TSTAMP + DATE_TYPE + NOT_NULL + COMMA_SEP +
	    Entry.COLUMN_NAME_MESSAGE + TEXT_TYPE + COMMA_SEP +
	    Entry.COLUMN_NAME_EXTRA + TEXT_TYPE +
	    " )";

	static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;	
	
	private Database() {}
	
}
