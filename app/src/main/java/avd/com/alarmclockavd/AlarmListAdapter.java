package avd.com.alarmclockavd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;


public class AlarmListAdapter extends BaseAdapter {
	private static boolean done = false;
	private Context ctx;
	private List<Alarm> alarmList;
	private AlarmsDataSource dataSource;

	public AlarmListAdapter (Context context, List<Alarm> alarms) {
		ctx = context;
		alarmList = alarms;
	}

	@Override
	public int getCount () {
		return alarmList.size ();
	}

	@Override
	public Alarm getItem (int position) {
		return alarmList.get (position);
	}

	@Override
	public long getItemId (int position) {
		return alarmList.get (position).getId ();
	}

	@Override
	public View getView (final int position, View convertView, final ViewGroup parent) {
		final View view;
		Holder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.alarm_row, parent, false);
			viewHolder = new Holder (view);
			view.setTag (viewHolder);
		} else {
			view = convertView;
		}
		viewHolder = (Holder) view.getTag ();
		view.setOnTouchListener (new OnSwipeTouchListener (ctx) {
			@Override
			public void onSwipeRight () {
				removeAlarm (view, position);
			}

			@Override
			public void onClick () {
				modifyAlarm (position, view);
			}

		});

		setViewHolderItems (position, viewHolder);
		return view;
	}

	private void modifyAlarm (int position, View view) {
		final Intent intent = new Intent (ctx, SetAlarmActivity.class);
		Alarm alarm = alarmList.get (position);
		cancelAlarm (alarm);
		intent.putExtra ("id", alarm.getId ());
		intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}


	public void refreshList (List<Alarm> list) {
		System.out.println("once");
		alarmList.clear ();
		alarmList.addAll(list);
		this.notifyDataSetChanged ();
	}

	//implements the toglebutton(checkbox) listener
	private CompoundButton.OnCheckedChangeListener checkBoxListener (final int position) {
		return new CompoundButton.OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					updateCheck (position, "active");
					setAlarm(alarmList.get(position));
				} else {
					updateCheck (position, " ");
					cancelAlarm(alarmList.get(position));
				}
			}
		};
	}

	private void removeAlarm (View view, int position) {
		Animation anim = AnimationUtils.loadAnimation (ctx, android.R.anim.slide_out_right);
		view.startAnimation (anim);
		dataSource = new AlarmsDataSource (ctx);
		dataSource.open ();
		cancelAlarm (alarmList.get (position));
		dataSource.deleteAlarm (alarmList.get (position));
		final Handler handler = new Handler ();
		// do something after animation finishes
		handler.postDelayed (new Runnable () {
			@Override
			public void run () {
				refreshList (dataSource.getAllAlarms ());
				dataSource.close ();
			}
		}, anim.getDuration ());

	}


    private void setViewHolderItems(int position, Holder viewHolder) {
	    Alarm alarm = alarmList.get (position);
	    viewHolder.alarmTime.setText (alarm.toString ());
	    viewHolder.description.setText (alarm.getDescription ());
	    viewHolder.days.setText (getWeekDays (alarm));
	    viewHolder.checkbox.setOnCheckedChangeListener (checkBoxListener (position));
	    checkboxUpdate (position, viewHolder);
    }

	//update checkbox for when items are reused or alarm is deleted
	private void checkboxUpdate (int position, Holder viewHolder) {
		if (alarmList.get (position).getActive ().equals ("active")) {
			viewHolder.checkbox.setChecked (true);
			setAlarm(alarmList.get(position));
		} else {
			viewHolder.checkbox.setChecked (false);
			cancelAlarm(alarmList.get(position));
		}
	}

	//updates the check field for the alarm in the database
	private void updateCheck (int position, String active) {
		dataSource = new AlarmsDataSource (ctx);
		dataSource.open ();
		dataSource.updateActive (alarmList.get (position).getId (), active);
		dataSource.close ();
	}


	public void setAlarm(Alarm alarm) {
		System.out.println("alarm set");
		String days = alarm.getDays();
		Intent intent = new Intent (ctx, AlarmReceiver.class);
		intent.putExtra ("requestCode", alarm.getId ());
		int id = (int) alarm.getId();
		for (int i = 0; i < days.length (); i++) {
			int day = parseInt(days.charAt(i) + "");
			switch (day) {
				case 0:
					setAlarmManager(alarm, day, id, intent);
					break;
				case 1:
					setAlarmManager(alarm, day, id, intent);
					break;
				case 2:
					setAlarmManager(alarm, day, id, intent);
					break;
				case 3:
					setAlarmManager(alarm, day, id, intent);
					break;
				case 4:
					setAlarmManager(alarm, day, id, intent);
					break;
				case 5:
					setAlarmManager(alarm, day, id, intent);
					break;
				case 6:
					setAlarmManager(alarm, day, id, intent);
					break;
				case 7:
					setAlarmManager(alarm, day, id, intent);
					break;

			}
		}
	}

	private void setAlarmManager(Alarm alarm, int day, int id, Intent intent) {
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = generateCalendar(alarm, day);
		PendingIntent pendingIntent = generatePendingIntent(day, id, intent);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	private PendingIntent generatePendingIntent(int day, int id, Intent intent) {
		return PendingIntent.getBroadcast(ctx, id + day * 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private Calendar generateCalendar(Alarm alarm, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay());
		calendar.set(Calendar.MINUTE, parseInt(alarm.getMinute()));
		calendar.set(Calendar.SECOND, 0);
		if (day != 0) {
			calendar.set(Calendar.DAY_OF_WEEK, day);
		}
		if (Calendar.getInstance().getTimeInMillis() > calendar.getTimeInMillis()) {
			if (day == 0) {
				calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK) + 1);
			} else {
				calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1);
			}
		}
		return calendar;
	}

	public void cancelAlarm(Alarm alarm) {
		System.out.println("alarm cancel");
		Intent intent = new Intent (ctx, AlarmReceiver.class);
		intent.putExtra ("requestCode", alarm.getId ());
		for (int i = 0; i <= 7000; i += 1000) {
			PendingIntent pi = PendingIntent.getBroadcast (ctx, i + (int) alarm.getId (), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) ctx.getSystemService (Context.ALARM_SERVICE);
			am.cancel (pi);
		}
	}

	private String getWeekDays (Alarm alarm) {
		String days = alarm.getDays ();
		StringBuilder builder = new StringBuilder ();
		if (days.charAt (0) == '0') {
			builder.append ("Once only");
		} else {
			builder.append ("Weekly: ");
		}
		for (int i = 0; i < days.length (); i++)
			switch (parseInt (days.charAt (i) + "")) {
				case 1:
					builder.append ("Sun ");
					break;
				case 2:
					builder.append ("Mon ");
					break;
				case 3:
					builder.append ("Tues ");
					break;
				case 4:
					builder.append ("Wed ");
					break;
				case 5:
					builder.append ("Thurs ");
					break;
				case 6:
					builder.append ("Fri ");
					break;
				case 7:
					builder.append ("Sat ");
					break;
		}
		return builder.toString ();
	}

	public class Holder {
		private TextView alarmTime;
		private TextView description;
		private TextView days;
		private ToggleButton checkbox;

		Holder(View v) {
			alarmTime = (TextView) v.findViewById(R.id.textViewTime);
			description = (TextView) v.findViewById(R.id.textViewDescription);
			days = (TextView) v.findViewById (R.id.textViewDays);
			checkbox = (ToggleButton) v.findViewById(R.id.checkBox);
		}
	}
}
