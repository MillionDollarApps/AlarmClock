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
        return alarmList.size();
    }

	@Override
	public Alarm getItem (int position) {
        return alarmList.get(position);
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

        setViewHolderItems(position, viewHolder);
        return view;
    }

	private void modifyAlarm (int position, View view) {
        //get the alarm item from the position
        Alarm alarm = alarmList.get(position);
        //build the intent to be run
        final Intent intent = new Intent(ctx, SetAlarmActivity.class);
        intent.putExtra("id", alarm.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //cancel the alarm
        cancelAlarm(alarm);
        //set up the animation
        Animation anim = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_in);
        view.startAnimation(anim);
        final Handler handler = new Handler();
        // do something after animation finishes
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //start the intent
                ctx.startActivity(intent);
            }
        }, anim.getDuration());
    }

    //refresh the list
    protected void refreshList(List<Alarm> list) {
        System.out.println("once");
        alarmList.clear();
        alarmList.addAll(list);
		this.notifyDataSetChanged ();
	}

	//implements the toglebutton(checkbox) listener
	private CompoundButton.OnCheckedChangeListener checkBoxListener (final int position) {
        final Alarm alarm = alarmList.get(position);
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
                    updateCheck(alarm, "active");
                    setAlarm(alarm);

				} else {
                    updateCheck(alarm, " ");
                    cancelAlarm(alarm);
                }
            }
        };
	}

    //removes the alarm
    private void removeAlarm(View view, int position) {
        //get the alarm object from the position
        Alarm alarm = alarmList.get(position);
        //set up the animation
        Animation anim = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_out_right);
        view.startAnimation(anim);
        //create and open the datasource in order to delete the alarm from the database
        dataSource = new AlarmsDataSource(ctx);
        dataSource.open();
        //cancel the alarm
        cancelAlarm(alarm);
        //delete the alarm from the database
        dataSource.deleteAlarm(alarm);
        final Handler handler = new Handler();
        // do something after animation finishes
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //refresh the list and close the datasource
                refreshList(dataSource.getAllAlarms());
                dataSource.close();
            }
        }, anim.getDuration());

	}


    private void setViewHolderItems(int position, Holder viewHolder) {
        //get alarm object from position
        Alarm alarm = alarmList.get(position);
        //set the text for the time, description, and days
        viewHolder.alarmTime.setText(alarm.toString());
        viewHolder.description.setText(alarm.getDescription());
        viewHolder.days.setText (getWeekDays (alarm));
        //set up listeners for the checkboxes, in order to set or cancel the alarm when the user interacts with the UI
        viewHolder.checkbox.setOnCheckedChangeListener(checkBoxListener(position));
        //update the checkbox as the user scrools down, or a new Instance of the list is created
        checkboxUpdate(alarm, viewHolder);
    }

	//update checkbox for when items are reused or alarm is deleted
    private void checkboxUpdate(final Alarm alarm, Holder viewHolder) {
        if (alarm.getActive().equals("active")) {
            viewHolder.checkbox.setChecked(true);
            //re-set the alarms
            setAlarm(alarm);
        } else {
            viewHolder.checkbox.setChecked(false);
        }
    }

	//updates the check field for the alarm in the database
    private void updateCheck(Alarm alarm, String active) {
        dataSource = new AlarmsDataSource(ctx);
        dataSource.open();
        dataSource.updateActive(alarm.getId(), active);
        dataSource.close();
    }


    protected void setAlarm(Alarm alarm) {
        System.out.println("alarm set");
        String days = alarm.getDays();
        int id = (int) alarm.getId();
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < days.length(); i++) {
            int day = parseInt(days.charAt(i) + "");
            switch (day) {
                case 0:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;
                case 1:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;
                case 2:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;
                case 3:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;
                case 4:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;
                case 5:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;
                case 6:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;
                case 7:
                    setAlarmManager(alarm, alarmManager, day, id);
                    break;

			}
		}
	}

    private void setAlarmManager(Alarm alarm, AlarmManager alarmManager, int day, int id) {
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.putExtra("requestCode", alarm.getId());
        intent.putExtra("day", day);
        Calendar calendar = generateCalendar(alarm, day);
        PendingIntent pendingIntent = generatePendingIntent(day, id, intent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	private PendingIntent generatePendingIntent(int day, int id, Intent intent) {
		return PendingIntent.getBroadcast(ctx, id + day * 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

    protected Calendar generateCalendar(Alarm alarm, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay());
        calendar.set(Calendar.MINUTE, parseInt(alarm.getMinute()));
		calendar.set(Calendar.SECOND, 0);
		if (day != 0) {
			calendar.set(Calendar.DAY_OF_WEEK, day);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
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

    protected void cancelAlarm(Alarm alarm) {
        System.out.println("alarm cancel");
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i <= 7000; i += 1000) {
            PendingIntent pi = PendingIntent.getBroadcast(ctx, i + (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pi);
        }
	}

    //sets up the weekdays textview from the listview row
    private String getWeekDays(Alarm alarm) {
        String days = alarm.getDays();
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

    private class Holder {
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
