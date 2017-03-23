package com.comon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeTool {
	public static Integer obtainDayGap ( String time1, String time2){
		SimpleDateFormat dateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
		int dayGap = 0;
		try {
			Date begin = dateFormat.parse( time1 );
			Date end = dateFormat.parse( time2 );
			
			long interval = ( end.getTime() - begin.getTime() )/ 1000;    //×ª»»³ÉÃë
			long day = interval / (24*3600);
			dayGap = (int)day;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dayGap;			
	}
}
