package avd.com.alarmclockavd.AlarmUtils;

import android.content.Context;
import android.widget.Toast;

import avd.com.alarmclockavd.Database.Alarm;

public class AlarmToast {

	private static final int DAY = 0;
	private static final int HOUR = 1;
	private static final int MINUTE = 2;
	private static final int SECOND = 3;
	private static long[] timeArray = new long[4];


	public static void show(Context context, Alarm alarm) {
		Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		toast.setText(buildText(alarm));
		toast.show();
	}

	private static String buildText(Alarm alarm) {
		AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
		calculateTime(alarmCalendar);
		return "Alarm is set to go off in " + getStringRepresentation(DAY, "day") + getStringRepresentation(HOUR, "hour") + getStringRepresentation(MINUTE, "minute") + getStringRepresentation(SECOND, "second");
	}

	private static void calculateTime(AlarmCalendar alarmCalendar) {
		long millis = alarmCalendar.getTimeDifferenceInMillis();
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		timeArray[DAY] = hours / 24;
		timeArray[HOUR] = hours - timeArray[DAY]*24;
		timeArray[MINUTE] = minutes - (timeArray[DAY]*24*60 + timeArray[HOUR]*60);
		timeArray[SECOND] = seconds - (timeArray[DAY]*24*60*60 + timeArray[HOUR]*60*60 + timeArray[MINUTE]*60);
	}


	private static String getStringRepresentation(int index, String unit) {
		long timeUnit = timeArray[index];
		String timeRepresentation = timeUnit + " " + unit;
		if(index == MINUTE && timeUnit == 0)
			return timeRepresentation + "s, ";
		if (timeUnit == 0)
			return "";
		if (index == SECOND)
			return timeRepresentation + "s.";
		if (timeUnit == 1)
			return timeRepresentation + ", ";
		else
			return timeRepresentation + "s, ";
	}




}
