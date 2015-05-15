package avd.com.alarmclockavd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static java.lang.Integer.parseInt;


public class AlarmReceiver extends BroadcastReceiver {

    private AlarmsDataSource dataSource;
    private long id;
    private Calendar cal;

    @Override
    public void onReceive(Context context, Intent intent) {
        id = intent.getExtras().getLong("requestCode");
        boolean oneTime = intent.getExtras().getBoolean("oneTime");
        System.out.println("onetime" + oneTime);
        dataSource = new AlarmsDataSource(context);
        dataSource.open();
        AlarmListAdapter adapter = new AlarmListAdapter(context, dataSource.getAllAlarms());
        if (oneTime) {
            dataSource.updateActive(id, " ");
            System.out.println("onetime");
            adapter.refreshList(dataSource.getAllAlarms());
            Intent isnt = new Intent(context, EnterText.class);
            isnt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(isnt);
        } else {
            setAlarm();
            System.out.println("repeating");
            if (System.currentTimeMillis() == cal.getTimeInMillis()) {
                Intent isnt = new Intent(context, EnterText.class);
                isnt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(isnt);
            }
        }
        dataSource.close();
    }

    private void setCalendar(int day) {
        Alarm alarm = dataSource.getAlarm(id);
        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, parseInt(alarm.getHour()));
        cal.set(Calendar.MINUTE, parseInt(alarm.getMinute()));
        cal.set(Calendar.AM_PM, alarm.getAmpm().equals("AM") ? 0 : 1);
        cal.set(Calendar.DAY_OF_WEEK, day);
        if (System.currentTimeMillis() > cal.getTimeInMillis())
            cal.set(Calendar.WEEK_OF_MONTH, Calendar.WEEK_OF_MONTH + 1);
    }

    private void setAlarm() {
        Alarm alarm = dataSource.getAlarm(id);
        for (int i = 0; i < alarm.getDays().length(); i++) {
            int day = parseInt(alarm.getDays().charAt(i) + "");
            System.out.println(day);
            System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "asd");
            if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == day) {
                setCalendar(day);
                break;
            }

        }
    }
}