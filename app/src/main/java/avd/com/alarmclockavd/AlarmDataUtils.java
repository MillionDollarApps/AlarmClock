package avd.com.alarmclockavd;


import android.content.Context;

import java.util.List;


public class AlarmDataUtils {

	private AlarmsDataSource dataSource;

	public AlarmDataUtils(Context context){
		dataSource = new AlarmsDataSource(context);
	}

	public List<Alarm> getAlarmList(){
		dataSource.open();
		List<Alarm> alarmList = dataSource.getAllAlarms();
		dataSource.close();
		return alarmList;
	}

	public void enableAlarm(Alarm alarm){
		dataSource.open();
		dataSource.enableAlarm(alarm);
		dataSource.close();
	}

	public void disableAlarm(Alarm alarm){
		dataSource.open();
		dataSource.disableAlarm(alarm);
		dataSource.close();
	}

	public void deleteAlarm(Alarm alarm){
		dataSource.open();
		dataSource.deleteAlarm(alarm);
		dataSource.close();
	}

	public Alarm createAlarm(Alarm alarm){
		dataSource.open();
		Alarm createdAlarm = dataSource.createAlarm(alarm);
		dataSource.close();
		return createdAlarm;
	}

	public void updateAlarm(Alarm alarm){
		dataSource.open();
		dataSource.updateAlarm(alarm);
		dataSource.close();
	}
}
