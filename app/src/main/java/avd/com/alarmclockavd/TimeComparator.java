package avd.com.alarmclockavd;

import java.util.Comparator;

public class TimeComparator implements Comparator<Alarm> {

	@Override
	public int compare(Alarm lhs, Alarm rhs) {
		AlarmUtils lhsTime = new AlarmUtils(lhs);
		AlarmUtils rhsTime = new AlarmUtils(rhs);
		int lTime = lhsTime.getTimeInMinutes();
		int rTime = rhsTime.getTimeInMinutes();
		if (lTime > rTime) {
			return 1;
		} else if (lTime < rTime) {
			return -1;
		}
		return 0;
	}
}
