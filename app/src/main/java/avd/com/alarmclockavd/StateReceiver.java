package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.List;


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