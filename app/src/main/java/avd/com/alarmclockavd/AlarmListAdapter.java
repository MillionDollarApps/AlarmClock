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
	private Context ctx;
	private LayoutInflater inflater;
	private List<Alarm> alarmList;
	private AlarmsDataSource dataSource;

	public AlarmListAdapter(Context context, List<Alarm> alarms){
		ctx = context;
		inflater = LayoutInflater.from(this.ctx);
		alarmList = alarms;
	}

	@Override
	public int getCount() {
		return alarmList.size();
	}

	public void refreshList(List<Alarm> list) {
		alarmList.clear();
		alarmList.addAll(list);
		this.notifyDataSetChanged();
	}

	@Override
	public Alarm getItem(int position) {
		return alarmList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return alarmList.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		Holder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_row, parent, false);
			viewHolder = new Holder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}
		viewHolder = (Holder) view.getTag();
		view.setOnTouchListener(new OnSwipeTouchListener(ctx) {
			@Override
			public void onSwipeRight() {
				removeAlarm(view, position);
			}

			@Override
			public void onSwipeLeft() {
				Intent intent = new Intent(ctx, SetAlarmActivity.class);
				Alarm alarm = alarmList.get(position);
				intent.putExtra("hour", alarm.getHour());
				intent.putExtra("minute", alarm.getMinute());
				intent.putExtra("ampm", alarm.getAmpm());
				intent.putExtra("days", alarm.getDays());
				intent.putExtra("id", alarm.getId());
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			}
		});
		setViewHolderItems(position, viewHolder);
		return view;
	}

	//implements the toglebutton(checkbox) listener
	private CompoundButton.OnCheckedChangeListener checkBoxListener(final int position) {
		final Alarm alarm = alarmList.get(position);
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					updateCheck(position, "active");
					if (alarm.getDays().equals("0")) {
						setOneTimeAlarm(alarm);
					} else
						setAllDaysAlarm(alarm);
				} else {
					updateCheck(position, " ");
					cancelAllDaysAlarm((int) alarm.getId(), alarm.getDays());
				}
			}
		};
	}

	private void removeAlarm(View view, int position) {
		Animation anim = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_out_right);
		view.startAnimation(anim);
		dataSource= new AlarmsDataSource(ctx);
		dataSource.open();
		dataSource.deleteAlarm(alarmList.get(position));
		final Handler handler = new Handler();
		// do something after animation finishes
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshList(dataSource.getAllAlarms());
				dataSource.close();
			}
		}, anim.getDuration());
	}

	private void setViewHolderItems(int position, Holder viewHolder) {
		viewHolder.alarmTime.setText(alarmList.get(position).toString());
		viewHolder.description.setText(alarmList.get(position).getDescription());
		viewHolder.checkbox.setOnCheckedChangeListener(checkBoxListener(position));
		checkboxUpdate(position, viewHolder);
	}

	//update checkbox for when items are reused or alarm is deleted
	private void checkboxUpdate(int position, Holder viewHolder) {
		if(alarmList.get(position).getActive().equals("active")) {
			viewHolder.checkbox.setChecked(true);
		}
		else
			viewHolder.checkbox.setChecked(false);
	}

	//updates the check field for the alarm in the database
	private void updateCheck(int position, String active) {
		dataSource = new AlarmsDataSource(ctx);
		dataSource.open();
		dataSource.updateActive(alarmList.get(position).getId(), active);
		refreshList(dataSource.getAllAlarms());
		dataSource.close();
	}

	private void setAlarm(Alarm alarm, int day, int id) {
		//Create an offset from the current time in which the alarm will go off.
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, parseInt(alarm.getHour()));
		cal.set(Calendar.AM_PM, alarm.getAmpm().equals("AM") ? 0 : 1);
		cal.set(Calendar.MINUTE, parseInt(alarm.getMinute()));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.DAY_OF_WEEK, day);
		//Create a new PendingIntent and add it to the AlarmManager
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		//postpone alarm for next week
		if (System.currentTimeMillis() > cal.getTimeInMillis()) {
			cal.set(Calendar.WEEK_OF_MONTH, Calendar.WEEK_OF_MONTH + 1);
		}
		//set up alarm
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
	}

	private void setOneTimeAlarm(Alarm alarm) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, parseInt(alarm.getHour()));
		cal.set(Calendar.AM_PM, alarm.getAmpm().equals("AM") ? 0 : 1);
		cal.set(Calendar.MINUTE, parseInt(alarm.getMinute()));
		cal.set(Calendar.SECOND, 0);
		//postpone alarm for next day
		if (System.currentTimeMillis() > cal.getTimeInMillis()) {
			cal.set(Calendar.DAY_OF_WEEK, cal.get(Calendar.DAY_OF_WEEK) + 1);
		}
		//Create a new PendingIntent and add it to the AlarmManager
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		intent.putExtra("requestCode", alarm.getId());
		PendingIntent pi = PendingIntent.getBroadcast(ctx, (int) alarm.getId(), intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		//set up alarm
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);

	}

	private void setAllDaysAlarm(Alarm alarm) {
		String days = alarm.getDays();
		for (int i = 0; i < days.length(); i++)
			switch (parseInt(days.charAt(i) + "")) {
				case 1:
					setAlarm(alarm, 1, (int) alarm.getId() + 1000);
					break;
				case 2:
					setAlarm(alarm, 2, (int) alarm.getId() + 2000);

					break;
				case 3:
					setAlarm(alarm, 3, (int) alarm.getId() + 3000);

					break;
				case 4:
					setAlarm(alarm, 4, (int) alarm.getId() + 4000);

					break;
				case 5:
					setAlarm(alarm, 5, (int) alarm.getId() + 5000);

					break;
				case 6:
					setAlarm(alarm, 6, (int) alarm.getId() + 6000);
					break;
				case 7:
					setAlarm(alarm, 7, (int) alarm.getId() + 7000);
					break;
			}
	}

	private void cancelAlarm(int id) {
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

	private void cancelOneTimeAlarm(long id) {
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		intent.putExtra("requestCode", id);
		PendingIntent pi = PendingIntent.getBroadcast(ctx, (int) id, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

	private void cancelAllDaysAlarm(int id, String days) {
		for (int i = 0; i < days.length(); i++)
			switch (parseInt(days.charAt(i) + "")) {
				case 1:
					cancelAlarm(id + 1000);
					break;
				case 2:
					cancelAlarm(id + 2000);
					break;
				case 3:
					cancelAlarm(id + 3000);
					break;
				case 4:
					cancelAlarm(id + 4000);
					break;
				case 5:
					cancelAlarm(id + 5000);
					break;
				case 6:
					cancelAlarm(id + 6000);
					break;
				case 7:
					cancelAlarm(id + 7000);
					break;
				default:
					cancelOneTimeAlarm((long) id);
					break;
			}
	}

	public class Holder {
		private TextView alarmTime;
		private TextView description;
		private ToggleButton checkbox;

		Holder(View v) {
			alarmTime = (TextView) v.findViewById(R.id.textViewTime);
			description = (TextView) v.findViewById(R.id.textViewDescription);
			checkbox = (ToggleButton) v.findViewById(R.id.checkBox);
		}
	}
}
