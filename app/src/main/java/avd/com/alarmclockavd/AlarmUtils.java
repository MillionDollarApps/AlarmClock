package avd.com.alarmclockavd;

import java.util.Calendar;


public class AlarmUtils {

	private final Alarm alarm;

	AlarmUtils(Alarm alarm) {
		this.alarm = alarm;
	}

	//gets the weekdays string reprezentations to set up the textview from the listview row
	public String getWeekDaysRepresentationAdapter() {
		String[] daysOfWeek = {"Sun ", "Mon ", "Tues ", "Wed ", "Thurs ", "Fri ", "Sat"};
		int days = alarm.getDays();
		String weekDays = "Weekly: ";
		if (days == 0) {
			weekDays = "Once only";
		}
		for (String aDaysOfWeek : daysOfWeek) {
			if ((days & 1) == 1) {
				weekDays += aDaysOfWeek;
			}
			days >>= 1;
		}
		return weekDays;
	}

	public String getWeekDayrepresentation() {
		String[] daysOfWeek = {"Sunday ", "Monday ", "Tuesday ", "Wednesday ", "Thursday ",
				"Friday ", "Saturday"};
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		return daysOfWeek[day - 1];
	}

	public int getTimeInMinutes() {
		return getHourOfDay() * 60 + alarm.getMinute();
	}

	public int getHourOfDay() {
		int hour = alarm.getHour();
		int hours;
		if (alarm.isAmpm()) {
			hours = hour % 12;
		} else {
			hours = hour < 12 ? hour + 12 : hour;
		}
		return hours;
	}

	public boolean isGoodTime() {
		Calendar calendar = getTimeCalendar();
		Calendar currentCalendar = Calendar.getInstance();
		int calendarMinutes = getTimeInMinutes(calendar);
		int currentCalendarMinutes = getTimeInMinutes(currentCalendar);
		//this logic prevents the alarm from going off if the user changes the date manually
		return currentCalendarMinutes == calendarMinutes;
	}

	public Calendar getTimeCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, getHourOfDay());
		calendar.set(Calendar.MINUTE, alarm.getMinute());
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

	private int getTimeInMinutes(Calendar calendar) {
		return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
	}

	public boolean isExpired() {
		Calendar calendar = getTimeCalendar();
		Calendar currentCalendar = Calendar.getInstance();
		return currentCalendar.getTimeInMillis() > calendar.getTimeInMillis();
	}

}
