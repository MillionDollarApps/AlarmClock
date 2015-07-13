package avd.com.alarmclockavd;

import java.util.Calendar;

import static java.util.Calendar.*;


/**
 * The type Alarm calendar.
 */
public class AlarmCalendar {

	private Calendar calendar;
	private Calendar currentCalendar;
	private Alarm alarm;

	/**
	 * Instantiates a new Alarm calendar.
	 *
	 * @param alarm the alarm
	 */
	public AlarmCalendar(Alarm alarm) {
        calendar = getInstance();
        currentCalendar = getInstance();
		this.alarm = alarm;
    }


	/**
	 * Get time in millis.
	 *
	 * @return the long
	 */
	public long getTimeInMillis() {
		setCalendarTime();
		setCalendarDayOfYear();
		return calendar.getTimeInMillis();
	}

	/**
	 * Is expired.
	 *
	 * @return the boolean
	 */
	public boolean isExpired() {
		setCalendarTime();
		return currentCalendar.getTimeInMillis() > calendar.getTimeInMillis();
    }

	/**
	 * Is time.
	 *
	 * @return the boolean
	 */
	public boolean isTime() {
		setCalendarTime();
		setCalendarDayOfYear();
		//this logic prevents the alarm from going off if the user changes the date manually
        return calendar.getTimeInMillis()==currentCalendar.getTimeInMillis();
    }

	/**
	 * Get time in minutes.
	 *
	 * @return the int
	 */
	public int getTimeInMinutes() {
		setCalendarTime();
		return calendar.get(HOUR_OF_DAY)*60 + calendar.get(MINUTE);
    }


	public String getWeekDaysRepresentation() {
		String[] daysOfWeek = {"Sun ", "Mon ", "Tues ", "Wed ", "Thurs ", "Fri ", "Sat"};
		int days = alarm.getDays();
		String weekDays = "";
		for (String day : daysOfWeek) {
			weekDays+= (days&1) == 1 ? day : "";
			days >>= 1;
		}
		return weekDays;
	}

	public String getWeekDay(){
		String[] daysOfWeek = {"Sunday", "Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
		return daysOfWeek[getCurrentDay()-1];
	}

	public long getTimeDifferenceInMillis(){
		setCalendarTime();
		setCalendarDayOfYear();
		return calendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
	}

	/////////////////////////////////////
	//
	/////////////////////////////////////

	private void setCalendarTime() {
        calendar.set(HOUR_OF_DAY, calculateTheHourOfTheDay());
        calendar.set(MINUTE, alarm.getMinute());
        calendar.set(SECOND, 0);
    }

	private void setCalendarDayOfYear() {
	    calendar.add(DAY_OF_YEAR, calculateDaysDifference());
    }

	private int calculateDaysDifference(){
		int currentDay = getCurrentDay();
		System.out.println(calculateDayOfAlarm(currentDay) - currentDay);
		return calculateDayOfAlarm(currentDay) - currentDay;
	}

	private int getCurrentDay() {
        return currentCalendar.get(DAY_OF_WEEK);
    }

	private int calculateDayOfAlarm(int currentDay) {
        if(alarm.getDays()==0)
           return calculateOneTimeAlarmDay(currentDay);
        else
           return calculateRepeatingAlarmDay(alarm, currentDay);
    }

	private int calculateOneTimeAlarmDay(int currentDay) {
		if (alarm.getDays() == 0 && !isExpired())
			return currentDay;
		else {
			return currentDay + 1;
		}
	}

	private int calculateRepeatingAlarmDay(Alarm alarm, int currentDay) {
        if (isCurrentDayStillAvailable(alarm, currentDay))
            return currentDay;
        else
            return calculateNextAvailableDayOfAlarm(alarm.getDays(), currentDay);
    }

	private boolean isCurrentDayStillAvailable(Alarm alarm, int currentDay) {
		return (alarm.getDays()>>(currentDay - 1) & 1) == 1 && !isExpired();
	}

	private int calculateNextAvailableDayOfAlarm(int daySet, int currentDay) {
		int nextDay = currentDay + 7;
		for (int i = currentDay; i < nextDay; i++)
			if (isNextDayAvailable(daySet, i)) {
				nextDay =i+1;
				break;
			}
		return nextDay;
	}

	private boolean isNextDayAvailable(int daySet, int i) {
        return (daySet & 1<<(i%8)) >0;
    }

	private int calculateTheHourOfTheDay() {
        int hour = alarm.getHour();
        if (alarm.isAmpm())
            hour %= 12;
        else hour = hour < 12 ? hour + 12 : hour;
        return hour;
    }


}
