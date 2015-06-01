package avd.com.alarmclockavd;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;
import java.util.List;


public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmsDataSource dataSource = new AlarmsDataSource(context);
        dataSource.open();
        List<Alarm> alarms = dataSource.getAllAlarms();
        AlarmListAdapter adapter = new AlarmListAdapter(context, alarms);
        System.out.print(intent.getAction() + " ");
        String action = intent.getAction();
        //this means that the hour has changed, or the phone has been rebooted, so we re-set the alarms
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_LOCALE_CHANGED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action) || Intent.ACTION_DATE_CHANGED.equals(action)) {

            setAlarms(adapter, alarms);
        } else {
            long id = intent.getExtras().getLong("requestCode");
            int day = intent.getExtras().getInt("day");
            Calendar calendar;
            Alarm alarm = dataSource.getAlarm(id);
            calendar = adapter.generateCalendar(alarm, day);
            System.out.println(calendar.get(Calendar.DAY_OF_YEAR) + " " + Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
            if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                System.out.print("here");
                setIntent(context, id);
            }
            if (Calendar.getInstance().getTimeInMillis() < calendar.getTimeInMillis() && day == 0) {
                dataSource.updateActive(id, " ");
                adapter.cancelAlarm(alarm);
            }
            setAlarms(adapter, dataSource.getAllAlarms());
        }

        dataSource.close();

    }


    private void setAlarms(AlarmListAdapter adapter, List<Alarm> alarms) {
        for (Alarm alarm : alarms)
            if (alarm.getActive().equals("active"))
                adapter.setAlarm(alarm);
    }

    private void setIntent(Context context, long id) {
        Intent alarmIntent = new Intent(context, EnterText.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra("id", id);
        context.startActivity(alarmIntent);
    }

}