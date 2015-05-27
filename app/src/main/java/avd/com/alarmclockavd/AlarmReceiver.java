package avd.com.alarmclockavd;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.List;


public class AlarmReceiver extends WakefulBroadcastReceiver {

	private long id;
	@Override
	public void onReceive (Context context, Intent intent) {
		//intent is fired up by the pendingIntent
		if (intent.getExtras() != null) {
			id = intent.getExtras().getLong("requestCode");
			AlarmsDataSource dataSource = new AlarmsDataSource(context);
			dataSource.open();
			Alarm alarm = dataSource.getAlarm(id);
			boolean oneTime = alarm.getDays().charAt(0) == '0';
			//if the alarm is not repeating, disable it after playing once
			if (oneTime) {
				dataSource.updateActive(id, " ");
				setIntent(context);
			} else {
				setIntent(context);
			}
			dataSource.close();
		}
		//this means that the hour has changed, or the phone has been rebooted, so we re-set the alarms
		else {
			AlarmsDataSource dataSource = new AlarmsDataSource(context);
			dataSource.open();
			List<Alarm> alarms = dataSource.getAllAlarms();
			AlarmListAdapter adapter = new AlarmListAdapter(context, alarms);
			for (Alarm alarm : alarms)
				if (alarm.getActive().equals("active"))
					adapter.setAlarm(alarm);
			dataSource.close();
		}

	}

	private void setIntent(Context context) {
		Intent alarmIntent = new Intent (context, EnterText.class);
		alarmIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtra("id", id);
		context.startActivity (alarmIntent);
	}

}