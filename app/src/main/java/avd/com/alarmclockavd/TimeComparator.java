package avd.com.alarmclockavd;

import java.util.Comparator;

public class TimeComparator implements Comparator<Alarm> {

	@Override
	public int compare(Alarm lhs, Alarm rhs) {
		if (lhs.getTime() > rhs.getTime()) {
			return 1;
		} else if (lhs.getTime() < rhs.getTime()) {
			return -1;
		}
		return 0;
	}
}
