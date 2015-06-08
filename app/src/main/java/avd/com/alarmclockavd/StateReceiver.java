package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;


public class StateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmsDataSource dataSource = new AlarmsDataSource(context);
		System.out.println("State");
		dataSource.open();
		List<Alarm> alarms = dataSource.getAllAlarms();
		AlarmListAdapter adapter = new AlarmListAdapter(context, alarms);
		setAlarms(adapter, alarms);
		dataSource.close();
	}

	private void setAlarms(AlarmListAdapter adapter, List<Alarm> alarms) {
		for (Alarm alarm : alarms) {
			if (alarm.getActive().equals("active")) {
				adapter.setAlarm(alarm);
			}
		}
	}
}
