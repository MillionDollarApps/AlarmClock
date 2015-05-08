package avd.com.alarmclockavd;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;




public class AlarmListAdapter extends BaseAdapter {
	private Context ctx;
	private LayoutInflater inflater;
	private List<Alarm> alarmList;
    private AlarmsDataSource dataSource;
	List<PendingIntent> pendingIntent = new ArrayList<>();

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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		Holder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_row, parent, false);
			viewHolder = new Holder(view);
			view.setTag(viewHolder);
		}
		else {
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

			}
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return getGestureDetector().onTouchEvent(event);
			}
		});
		setViewHolderItems(position, viewHolder);
		checkboxUpdate(position, viewHolder);
		return view;
	}

	private CompoundButton.OnCheckedChangeListener checkBoxListener(final int position) {
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					update(position, "active");
				} else {
					update(position, " ");
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
		handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                refreshList(dataSource.getAllAlarms());
                dataSource.close();
            }
        }, anim.getDuration());
	}

	private void setViewHolderItems(int position, Holder viewHolder) {
		viewHolder.alarmTime.setText(alarmList.get(position).toString());
		viewHolder.description.setText(alarmList.get(position).getDescription());
		viewHolder.checkbox.setOnCheckedChangeListener(checkBoxListener(position));
	}

	private void checkboxUpdate(int position, Holder viewHolder) {
		int hour = Integer.parseInt(alarmList.get(position).getHour());
		int minute = Integer.parseInt(alarmList.get(position).getMinute());

		if(alarmList.get(position).getActive().equals("active")) {
			viewHolder.checkbox.setChecked(true);
		}
		else
			viewHolder.checkbox.setChecked(false);
	}

	private void update(int position,String active) {
		dataSource = new AlarmsDataSource(ctx);
		dataSource.open();
		dataSource.update(alarmList.get(position), active);
		refreshList(dataSource.getAllAlarms());
		dataSource.close();

	}


	private void setAlarm(int x, int y, int i){
		//Create an offset from the current time in which the alarm will go off.
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, x);
		cal.set(Calendar.MINUTE, y);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.DAY_OF_WEEK, 5);


		Toast.makeText(ctx, x + " " + y, Toast.LENGTH_SHORT).show();
		//Create a new PendingIntent and add it to the AlarmManager
		Intent intent = new Intent(ctx, EnterText.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		pendingIntent.add(PendingIntent.getActivity(ctx, i, intent, PendingIntent.FLAG_ONE_SHOT));

		AlarmManager am = (AlarmManager) ctx.getSystemService(Activity.ALARM_SERVICE);
		for(int z = 0;z<pendingIntent.size();z++) {
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent.get(z));
		}
		if(Calendar.getInstance().getTimeInMillis()>cal.getTimeInMillis())
		{
			am.cancel(pendingIntent.get(0));
		}
	}

	private void cancelAlarm(){

	}

}


