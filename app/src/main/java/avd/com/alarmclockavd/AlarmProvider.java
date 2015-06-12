package avd.com.alarmclockavd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmProvider {
	private final Context context;
	private final Alarm alarm;
	private boolean isBad;

	AlarmProvider(Context context, Alarm alarm) {
		this.context = context;
		this.alarm = alarm;
	}

	public void setAlarm() {
		System.out.println("set alarm");
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("alarm", alarm);
		System.out.println(alarm.getId());
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		//sets the Calendar
		Calendar alarmCalendar = getCalendar();
		System.out.println(alarmCalendar.get(Calendar.DAY_OF_WEEK));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) alarm.getId(),
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (!isBad) {
			alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
		}
	}

	private Calendar getCalendar() {
		AlarmUtils alarmUtils = new AlarmUtils(alarm);
		Calendar alarmCalendar = alarmUtils.getTimeCalendar();
		Calendar currentCalendar = Calendar.getInstance();
		//set initial calendar
		setCurrentDateCalendar(alarmCalendar);
		// checks to see if the alarm is expired or not, and in the case it has expired it set
		// the day for the alarm to the nextday if oneTime or next week if repeating
		if (currentCalendar.getTimeInMillis() > alarmCalendar.getTimeInMillis()) {
			setFutureDateCalendar(alarmCalendar);
		}
		return alarmCalendar;
	}

	private void setCurrentDateCalendar(Calendar alarmCalendar) {
		//aliases
		int dayOfWeek = Calendar.DAY_OF_WEEK;
		int currentDay = alarmCalendar.get(dayOfWeek);
		int alarmDay = getDayOfAlarm(currentDay);
		if (alarmDay == 0) {
			alarmCalendar.set(dayOfWeek, currentDay);
		} else {
			// sets up the calendar to have the required day of the week
			alarmCalendar.set(dayOfWeek, alarmDay);
		}
	}

	public int getDayOfAlarm(int currentDay) {
		int day = alarm.getDays();
		if (day == 0) {
			return day;
		}
		day >>= (currentDay - 1);
		if ((day & 1) == 1) {
			return currentDay;
		} else {
			day >>= 1;
			day >>= currentDay;
			int nextDay = currentDay + 1;
			for (int i = 1; i <= 7; i++) {
				if ((day & 1) != 1) {
					day >>= 1;
					nextDay++;
				}
				if (nextDay == 8) {
					day = alarm.getDays();
					nextDay = 1;
				}
			}
			return nextDay;
		}
	}

	private void setFutureDateCalendar(Calendar alarmCalendar) {
		//aliases
		int dayOfWeek = Calendar.DAY_OF_WEEK;
		int currentDay = alarmCalendar.get(dayOfWeek);
		int alarmDay = getDayOfAlarm(currentDay);
		int alarmNextDay = getNextDayOfAlarm(currentDay);
		if (alarmDay == 0) {
			alarmCalendar.set(dayOfWeek, (currentDay + 1) % 8);
		} else {
			if (alarmDay == alarmNextDay) {
				isBad = true;
			} else {
				alarmCalendar.set(dayOfWeek, alarmNextDay);
			}
		}
	}

	public int getNextDayOfAlarm(int currentDay) {
		int day = alarm.getDays();
		day >>= currentDay;
		int nextDay = currentDay + 1;
		for (int i = 1; i <= 7; i++) {
			if ((day & 1) != 1) {
				day >>= 1;
				nextDay++;
			}
			if (nextDay == 8) {
				day = alarm.getDays();
				nextDay = 1;
			}
		}
		return nextDay;
	}

	public void cancelAlarm() {
		System.out.println("alarm cancel");
		Intent intent = new Intent(context, AlarmReceiver.class);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(context, (int) alarm.getId(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		am.cancel(pi);
		pi.cancel();
	}


}