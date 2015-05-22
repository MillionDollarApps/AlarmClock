package avd.com.alarmclockavd;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;


public class AlarmReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive (Context context, Intent intent) {
		long id = intent.getExtras ().getLong ("requestCode");

		System.out.println ("here");
		AlarmsDataSource dataSource = new AlarmsDataSource (context);
		dataSource.open ();
		AlarmListAdapter adapter = new AlarmListAdapter (context, dataSource.getAllAlarms ());
		Alarm alarm = dataSource.getAlarm (id);
		boolean oneTime = alarm.getDays ().charAt (0) == '0';
		if (oneTime) {
			dataSource.updateActive (id, " ");
			adapter.refreshList (dataSource.getAllAlarms ());
			setIntent (context, alarm);
		} else {
				setIntent (context, alarm);
		}
		dataSource.close ();
	}

	private void setIntent (Context context, Alarm alarm) {
		Intent alarmIntent = new Intent (context, EnterText.class);
		alarmIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtra ("description", alarm.getDescription ());
		alarmIntent.putExtra ("uri", alarm.getRingtone ());
		alarmIntent.putExtra ("vibrate", alarm.getVibrate ());
		alarmIntent.putExtra ("time", alarm.toString ());
		context.startActivity (alarmIntent);
	}
//
//	private boolean isTime (Alarm alarm) {
//		Calendar cal = Calendar.getInstance ();
//		for (int i = 0; i < alarm.getDays ().length (); i++) {
//			int day = parseInt (alarm.getDays ().charAt (i) + "");
//			if (cal.get (Calendar.DAY_OF_WEEK) == day) {
//				cal.set (Calendar.HOUR_OF_DAY, alarm.getHourOfDay ());
//				cal.set (Calendar.MINUTE, parseInt (alarm.getMinute ()));
////				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//					cal.set (Calendar.SECOND, 0);
////				}
//				cal.set (Calendar.DAY_OF_WEEK, day);
//				break;
//			}
//		}
//		return Calendar.getInstance ().getTime ().equals (cal.getTime ());
//	}

}