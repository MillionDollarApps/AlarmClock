package avd.com.alarmclockavd.Database;

import java.util.Comparator;

import avd.com.alarmclockavd.AlarmUtils.AlarmCalendar;

/**
 * The type Time comparator.
 */
public class TimeComparator implements Comparator<Alarm> {

	@Override
	public int compare(Alarm lhs, Alarm rhs) {
        AlarmCalendar lhsAlarmCalendar = new AlarmCalendar(lhs);
		AlarmCalendar rhsAlarmCalendar = new AlarmCalendar(rhs);
		int lTime = lhsAlarmCalendar.getTimeInMinutes();
		int rTime = rhsAlarmCalendar.getTimeInMinutes();
		if (lTime > rTime) {
			return 1;
		} else if (lTime < rTime) {
			return -1;
		}
		return 0;
	}
}
