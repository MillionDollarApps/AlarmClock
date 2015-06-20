package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class AlarmReceiver extends BroadcastReceiver {

	private Alarm alarm;

	@Override
	public void onReceive(Context context, Intent intent) {
        System.out.println("received");
		Bundle extras = intent.getExtras();
		alarm = extras.getParcelable("alarm");
		AlarmUtils alarmUtils = new AlarmUtils(alarm);
		if (alarmUtils.isGoodTime()) {
			setIntent(context);
		}
	}

	private void setIntent(Context context) {
		Intent alarmIntent = new Intent(context, EnterText.class);
		alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtra("alarm", alarm);
		context.startActivity(alarmIntent);
	}


}