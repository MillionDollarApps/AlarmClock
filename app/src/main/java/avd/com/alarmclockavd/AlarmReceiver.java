package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;


public class AlarmReceiver extends BroadcastReceiver {

	private boolean oneTime;

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmsDataSource dataSource = new AlarmsDataSource(context);
		dataSource.open();
		List<Alarm> alarms = dataSource.getAllAlarms();
		AlarmListAdapter adapter = new AlarmListAdapter(context, alarms);
		System.out.print("alarmReceived ");
		long id = intent.getExtras().getLong("requestCode");
		int dayofyear = intent.getExtras().getInt("dayofyear");
		int day = intent.getExtras().getInt("day");
		Alarm alarm = dataSource.getAlarm(id);
		Calendar calendar = generateCalendar(alarm, dayofyear, day);
		Calendar currentCalendar = Calendar.getInstance();
		int calendarMinutes = getTimeInMinutes(calendar);
		int currentCalendarMinutes = getTimeInMinutes(currentCalendar);
		if (day == 0) {
			if (calendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)) {
				if (currentCalendarMinutes > calendarMinutes) {
					dataSource.updateActive(id, " ");
					adapter.cancelAlarm(alarm);
				} else {
					setIntent(context, id);
					System.out.println("once!!");
				}
			}
		} else {
			if (calendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)) {
				setIntent(context, id);
				System.out.println("dos!!");
			}
		}
		if (!oneTime) {
			setAlarm(adapter, alarm);
		}
		dataSource.close();
	}

	private Calendar generateCalendar(Alarm alarm, int dayofyear, int day) {
		Calendar calendar = Calendar.getInstance();
		Calendar currentCalendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay());
		calendar.set(Calendar.MINUTE, parseInt(alarm.getMinute()));
		calendar.set(Calendar.SECOND, 0);
		int calendarMinutes = getTimeInMinutes(calendar);
		int currentCalendarMinutes = getTimeInMinutes(currentCalendar);
		if (day == 0) {
			calendar.set(Calendar.DAY_OF_YEAR, dayofyear);
		} else {
			if (currentCalendarMinutes > calendarMinutes) {
				calendar.set(Calendar.DAY_OF_YEAR, currentCalendar.get(Calendar.DAY_OF_YEAR) + 7);
			} else if (currentCalendarMinutes < calendarMinutes) {
				calendar.set(Calendar.DAY_OF_YEAR, currentCalendar.get(Calendar.DAY_OF_YEAR) + 7);
			} else {
				calendar.set(Calendar.DAY_OF_YEAR, dayofyear);
			}
		}
		return calendar;
	}

	private int getTimeInMinutes(Calendar calendar) {
		return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
	}

	private void setIntent(Context context, long id) {
		Intent alarmIntent = new Intent(context, EnterText.class);
		alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtra("id", id);
		context.startActivity(alarmIntent);
	}

	private void setAlarm(AlarmListAdapter adapter, Alarm alarm) {
		oneTime = true;
		if (alarm.getActive().equals("active")) {
			adapter.setAlarm(alarm);
		}
	}

}