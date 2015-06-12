package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;


public class StateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.print("received date.. w/e");
		System.out.print(intent.getAction());
		System.out.println("serviceregister");
		AlarmsDataSource dataSource = new AlarmsDataSource(context);
		dataSource.open();
		List<Alarm> alarms = dataSource.getAllAlarms();
		for (Alarm alarm : alarms) {
			if (alarm.isActive() && alarm.getDays() != 0) {
				AlarmProvider alarmProvider = new AlarmProvider(context, alarm);
				alarmProvider.setAlarm();
			} else if (alarm.isActive()) {
				AlarmProvider alarmProvider = new AlarmProvider(context, alarm);
				AlarmUtils alarmUtils = new AlarmUtils(alarm);
				if (alarmUtils.isExpired()) {
					dataSource.updateActive(alarm.getId(), false);
					alarmProvider.cancelAlarm();
				}
			}
		}
		dataSource.close();
	}
}