package avd.com.alarmclockavd;

import android.app.IntentService;
import android.content.Intent;

import java.util.Collections;
import java.util.List;

/**
 * The type State receiver service.
 */
public class StateReceiverService extends IntentService{

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public StateReceiverService(String name) {
        super(name);
    }

	/**
	 * Instantiates a new State receiver service.
	 */
	public StateReceiverService(){
        super("StateReceiverService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
	    AlarmDataUtils dataUtils = new AlarmDataUtils(this);
        List<Alarm> alarms = dataUtils.getAlarmList();
        for (Alarm alarm : alarms)
            if (alarm.isActive())
	            setAlarm(dataUtils, alarm);
        StateReceiver.completeWakefulIntent(intent);
    }

	private void setAlarm(AlarmDataUtils dataUtils, Alarm alarm) {
		AlarmFunctions alarmFunctions = new AlarmFunctions(this);
		AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
		if (alarm.getDays() == 0 && alarmCalendar.isExpired()){
	        dataUtils.disableAlarm(alarm);
	        alarmFunctions.cancelAlarm(alarm);
	    }
	    else alarmFunctions.setAlarm(alarm);
	}

}
