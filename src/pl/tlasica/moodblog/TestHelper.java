package pl.tlasica.moodblog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class TestHelper {

	public static void forTestCreateHistoryOfMood(DatabaseHelper db, int numdays) {
		Calendar now = GregorianCalendar.getInstance();
		Random rand = new Random();
		for(int minusDays=1; minusDays<numdays; minusDays++) {
			now.add(Calendar.DAY_OF_YEAR, -1);
			Date date = now.getTime();
			
			for(Mood mood: Mood.values()) {
				int count = rand.nextInt( 3 );
				String msg = "Random generated " + String.valueOf(count);
				for(int i=0; i<count; i++) {
					MoodEntry e = MoodEntry.create(mood.toString(), date, msg);
					db.saveEntry( e );
				}
			}
			
		}
	}

}
