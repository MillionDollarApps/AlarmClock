package avd.com.alarmclockavd.AlarmReceiversAndServices;

import android.app.IntentService;
import android.content.Intent;

import avd.com.alarmclockavd.AlarmUtils.AlarmCalendar;
import avd.com.alarmclockavd.Database.Alarm;
import avd.com.alarmclockavd.Database.AlarmDataUtils;
import avd.com.alarmclockavd.EnterTextActivity.EnterText;

/**
 * The type Alarm receiver service.
 */
public class AlarmReceiverService extends IntentService {
	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public AlarmReceiverService(String name) {
        super(name);
    }

	/**
	 * Instantiates a new Alarm receiver service.
	 */
	public AlarmReceiverService() {
        super("AlarmReceiverService");
    }

	private void setIntent(Alarm alarm) {
		Intent alarmIntent = new Intent(this, EnterText.class);
		alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtra("alarm", alarm);
		this.startActivity(alarmIntent);
	}

    @Override
    protected void onHandleIntent(Intent intent) {
	    long id = intent.getExtras().getLong("id");
	    AlarmDataUtils dataUtils = new AlarmDataUtils(this);
	    Alarm alarm = dataUtils.getAlarm(id);
	    AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
	    if (alarmCalendar.isTime())
		    setIntent(alarm);
        AlarmReceiver.completeWakefulIntent(intent);
    }
}
