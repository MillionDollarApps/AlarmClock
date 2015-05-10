package avd.com.alarmclockavd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;


public class AlarmListAdapter extends BaseAdapter {
	List<PendingIntent> pendingIntent = new ArrayList<>();
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

    public void refreshList(List<Alarm> list){
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
				//TODO
			}

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return getGestureDetector().onTouchEvent(event);
			}
		});
		setViewHolderItems(position, viewHolder);
		return view;
	}

	private CompoundButton.OnCheckedChangeListener checkBoxListener(final int position) {
		final Alarm alarm = alarmList.get(position);
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					update(position, "active");
					setAlarm(parseInt(alarm.getHour()), parseInt(alarm.getMinute()), alarm.getAmpm(), alarm.getDays(), (int) alarm.getId());
				} else {
					update(position, " ");
					cancelAlarm((int) alarm.getId());
				}
			}
		};
	}

	private void removeAlarm(View view, int position) {
		Animation anim = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_out);
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

	//updates the alarm ListView
	private void update(int position,String active) {
		dataSource = new AlarmsDataSource(ctx);
		dataSource.open();
		dataSource.update(alarmList.get(position), active);
		refreshList(dataSource.getAllAlarms());
		dataSource.close();
	}

	private void setAlarm(int hour, int minute, String ampm, String days, int id) {
		//Create an offset from the current time in which the alarm will go off.
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.AM_PM, ampm.equals("AM") ? 0 : 1);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.DAY_OF_WEEK, 1);

		//Create a new PendingIntent and add it to the AlarmManager
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

		if (System.currentTimeMillis() > cal.getTimeInMillis())
		{
			cal.set(Calendar.DAY_OF_WEEK, 2);
		}

		System.out.println(cal.get(Calendar.DAY_OF_WEEK));
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
	}

	private void cancelAlarm(int id) {
		Intent intent = new Intent(ctx, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctx, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
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


