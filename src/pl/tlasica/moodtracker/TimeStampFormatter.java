package pl.tlasica.moodtracker;

import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;

public class TimeStampFormatter {

	Context		mContext;
	
	public static TimeStampFormatter create(Context context) {
		TimeStampFormatter res = new TimeStampFormatter();
		res.mContext = context;		
		return res;
	}
	
	public String format(Date dt) {
		String date = DateFormat.getDateFormat(mContext).format(dt);
		String time = DateFormat.getTimeFormat(mContext).format(dt);		
		return date + " " + time;		
	}
	
}
