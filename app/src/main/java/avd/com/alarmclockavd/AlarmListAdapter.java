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
	public View getView (final int position, View convertView, ViewGroup parent) {
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
			public void onSwipeLeft () {
				Intent intent = new Intent (ctx, SetAlarmActivity.class);
				Alarm alarm = alarmList.get (position);
				intent.putExtra ("id", alarm.getId ());
				intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity (intent);
			}
		});
		setViewHolderItems (position, viewHolder);
		return view;
	}

	public void refreshList (List<Alarm> list) {
		alarmList.clear ();
		for (int i = 0; i < list.size (); i++) {
			if (list.get (i).getActive ().equals ("active")) {
				setAlarm (list.get (i));
			} else {
				cancelAlarm (list.get (i));
			}
			alarmList.add (list.get (i));
		}
		this.notifyDataSetChanged ();
	}

	//implements the toglebutton(checkbox) listener
	private CompoundButton.OnCheckedChangeListener checkBoxListener (final int position) {
		final Alarm alarm = alarmList.get (position);
		return new CompoundButton.OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					updateCheck (position, "active");
				} else {
					updateCheck (position, " ");
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
		} else {
			viewHolder.checkbox.setChecked (false);
		}
	}

	//updates the check field for the alarm in the database
	private void updateCheck (int position, String active) {
		dataSource = new AlarmsDataSource (ctx);
		dataSource.open ();
		dataSource.updateActive (alarmList.get (position).getId (), active);
		refreshList (dataSource.getAllAlarms ());
		dataSource.close ();
	}

	private void setAlarm (Alarm alarm) {
		boolean oneTime = alarm.getDays ().charAt (0) == '0';
		//Create an offset from the current time in which the alarm will go off.
		Calendar cal = Calendar.getInstance ();
		cal.set (Calendar.HOUR_OF_DAY, alarm.getHourOfDay ());
		cal.set (Calendar.MINUTE, parseInt (alarm.getMinute ()));
		cal.set (Calendar.SECOND, 0);
		if (!oneTime) {
			cal.set (Calendar.DAY_OF_WEEK, parseInt (alarm.getDays ().charAt (0) + ""));
		}
		//Create a new PendingIntent and add it to the AlarmManager
		if (oneTime && Calendar.getInstance ().getTimeInMillis () > cal.getTimeInMillis ()) {
			cal.set (Calendar.DAY_OF_WEEK, cal.get (Calendar.DAY_OF_WEEK) + 1);
		}
		Intent intent = new Intent (ctx, AlarmReceiver.class);
		intent.putExtra ("requestCode", alarm.getId ());
		intent.putExtra ("oneTime", oneTime);
		PendingIntent pi = PendingIntent.getBroadcast (ctx, (int) alarm.getId (), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService (Context.ALARM_SERVICE);
		//set up alarm
		if (oneTime) {
			am.set (AlarmManager.RTC_WAKEUP, cal.getTimeInMillis (), pi);
			System.out.println ("alarm set");
		} else {
			am.setRepeating (AlarmManager.RTC_WAKEUP, cal.getTimeInMillis (), AlarmManager.INTERVAL_DAY, pi);
			System.out.println ("rep alarm set");
		}
	}


	private void cancelAlarm (Alarm alarm) {
		Intent intent = new Intent (ctx, AlarmReceiver.class);
		boolean oneTime = alarm.getDays ().charAt (0) == '0';
		intent.putExtra ("requestCode", alarm.getId ());
		intent.putExtra ("oneTime", oneTime);
		PendingIntent pi = PendingIntent.getBroadcast (ctx, (int) alarm.getId (), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService (Context.ALARM_SERVICE);
		am.cancel (pi);
		System.out.println ("alarm canceled");
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
