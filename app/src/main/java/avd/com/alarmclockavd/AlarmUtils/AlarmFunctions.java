package avd.com.alarmclockavd.AlarmUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import avd.com.alarmclockavd.AlarmReceiversAndServices.AlarmReceiver;
import avd.com.alarmclockavd.Database.Alarm;
/**
 * The type Alarm provider.
 */
public class AlarmFunctions {
	private final Context context;

	/**
	 * Instantiates a new Alarm provider.
	 *
	 * @param context the context
	 */
	public AlarmFunctions(Context context) {
		this.context = context;
	}

	/**
	 * Sets alarm.
	 */
	public void setAlarm(Alarm alarm) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", alarm.getId());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //sets the Calendar
        AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) alarm.getId(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
    }


	/**
	 * Cancel alarm.
	 */
	public void cancelAlarm(Alarm alarm) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(context, (int) alarm.getId(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		am.cancel(pi);
	}


}