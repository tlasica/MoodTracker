package pl.tlasica.moodblog;

import java.util.Date;

public class MoodEntry {

	Mood 			mood;
	String 			message;
	Date			tstamp;
	
	public static MoodEntry create(String moodStr, long dtMillis, String message) {
		Date date = new Date();
		date.setTime( dtMillis );
		return create(moodStr, date, message);
	}

	public static MoodEntry create(String moodStr, Date dt, String message) {
		MoodEntry e = new MoodEntry();
		e.mood = Mood.fromString(moodStr);
		e.tstamp = dt;
		e.message = message;
		return e;
	}
	
	public static MoodEntry createNow(Mood mood, String message) {
		MoodEntry e = new MoodEntry();
		e.mood = mood;
		e.tstamp = new Date();
		e.message = message;
		return e;
	}
	
}
