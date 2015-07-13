package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;


/**
 * The type Alarm receiver.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmReceiverService.class);
        long id = intent.getExtras().getLong("id");
        service.putExtra("id", id);
        startWakefulService(context,service);
	}

}