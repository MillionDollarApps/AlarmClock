package avd.com.alarmclockavd;

import android.content.Context;
import android.widget.Toast;

public class AlarmToast {

	private long[] timeArray;
	private Toast toast;
	private final int DAY = 0;
	private final int HOUR = 1;
	private final int MINUTE = 2;
	private final int SECOND = 3;

	AlarmToast(Context context){
		toast = Toast.makeText(context,"",Toast.LENGTH_LONG);
		timeArray = new long[4];
	}

	public void show(Alarm alarm){
		toast.setText(buildText(alarm));
		toast.show();
	}

	private String buildText(Alarm alarm) {
		AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
		calculateTime(alarmCalendar);
		StringBuilder builder = new StringBuilder();
		builder.append("Alarm is set to go off in ");
		builder.append(getStringRepresentation(DAY,"day"));
		builder.append(getStringRepresentation(HOUR,"hour"));
		builder.append(getStringRepresentation(MINUTE,"minute"));
		builder.append(getStringRepresentation(SECOND,"second"));
		return builder.toString();
	}

	private void calculateTime(AlarmCalendar alarmCalendar) {
		long millis = alarmCalendar.getTimeDifferenceInMillis();
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		timeArray[DAY] = hours / 24;
		timeArray[HOUR] = hours - timeArray[DAY]*24;
		timeArray[MINUTE] = minutes - (timeArray[DAY]*24*60 + timeArray[HOUR]*60);
		timeArray[SECOND] = seconds - (timeArray[DAY]*24*60*60 + timeArray[HOUR]*60*60 + timeArray[MINUTE]*60);
	}


	private String getStringRepresentation(int index, String unit) {
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
