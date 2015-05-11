package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getExtras().getLong("requestCode");
        System.out.println(id);
        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        dataSource.open();
        dataSource.updateActive(id, " ");
        AlarmListAdapter adapter = new AlarmListAdapter(context, dataSource.getAllAlarms());
        adapter.refreshList(dataSource.getAllAlarms());
        dataSource.close();
        Intent isnt = new Intent(context, EnterText.class);
        isnt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(isnt);
        /*// Vibrate the mobile phone
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);  */
    }
}