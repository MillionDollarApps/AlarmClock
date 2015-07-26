package avd.com.alarmclockavd.AlarmReceiversAndServices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;


/**
 * The type State receiver.
 */
public class StateReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, StateReceiverService.class);
        startWakefulService(context,service);
	}
}