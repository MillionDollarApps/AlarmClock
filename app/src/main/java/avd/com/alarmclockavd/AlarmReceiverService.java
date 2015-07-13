package avd.com.alarmclockavd;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

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

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        long id = extras.getLong("id");
        Alarm alarm = getAlarm(id);
        AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
        if (alarmCalendar.isTime())
            setIntent(alarm);
        AlarmReceiver.completeWakefulIntent(intent);
    }

	private Alarm getAlarm(long id){
		AlarmsDataSource dataSource = new AlarmsDataSource(this);
		dataSource.open();
		Alarm alarm = dataSource.getAlarm(id);
		dataSource.close();
		return alarm;
	}

    private void setIntent(Alarm alarm) {
        Intent alarmIntent = new Intent(this, EnterText.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra("alarm", alarm);
        this.startActivity(alarmIntent);
    }
}
