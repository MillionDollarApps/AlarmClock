package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;



public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ala e", Toast.LENGTH_SHORT).show();
        Intent isnt = new Intent(context, EnterText.class);
        isnt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(isnt);
        System.out.println("cacamaca");
        /*// Vibrate the mobile phone
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);  */
	}
}