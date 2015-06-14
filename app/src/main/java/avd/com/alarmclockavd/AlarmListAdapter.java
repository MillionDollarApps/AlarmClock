package avd.com.alarmclockavd;

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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;


public class AlarmListAdapter extends BaseAdapter {
	private Context context;
	private List<Alarm> alarmList;
	private AlarmsDataSource dataSource;
	private LayoutInflater layoutInflater;

	public AlarmListAdapter(Context context, List<Alarm> alarms) {
		this.context = context;
		alarmList = alarms;
		layoutInflater = LayoutInflater.from(context);
		dataSource = new AlarmsDataSource(context);
	}

	@Override
	public int getCount() {
		return alarmList.size();
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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final View view;
		Holder viewHolder;
		if (convertView == null) {
			view = layoutInflater.inflate(R.layout.alarm_row, parent, false);
			viewHolder = new Holder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}
		viewHolder = (Holder) view.getTag();
		setViewHolderItems(position, viewHolder);
		view.setOnTouchListener(rowViewTouchListener(position, view));
		return view;
	}

	private void setViewHolderItems(int position, Holder viewHolder) {
		//get alarm object from position
		Alarm alarm = alarmList.get(position);
		//get alarm utils to set up days
		AlarmUtils alarmUtils = new AlarmUtils(alarm);
		//set the text for the time, description, and days
		viewHolder.alarmTime.setText(alarm.toString());
		viewHolder.description.setText(alarm.getDescription());
		viewHolder.days.setText(alarmUtils.getWeekDaysRepresentationAdapter());
		//set up listeners for the checkboxes, in order to set or cancel the alarm when the user interacts with the UI
		//update the checkbox as the user scrools down, or a new Instance of the list is created
		viewHolder.checkbox.setOnCheckedChangeListener(checkBoxListener(position));
		checkboxUpdate(alarm, viewHolder);
	}

	//implements the toglebutton(checkbox) listener
	private CompoundButton.OnCheckedChangeListener checkBoxListener(final int position) {
		final Alarm alarm = alarmList.get(position);
		final AlarmProvider alarmProvider = new AlarmProvider(context, alarm);
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView.isPressed() && isChecked) {
					alarmProvider.setAlarm();
					Toast.makeText(context, "Alarm is set for " + alarm.toString(), Toast
							.LENGTH_LONG).show();
					updateCheck(alarm, true);
					refresh();
				} else if (buttonView.isPressed()) {
					alarmProvider.cancelAlarm();
					updateCheck(alarm, false);
					refresh();
				}
			}
		};
	}

	//updates the check field for the alarm in the database
	private void updateCheck(Alarm alarm, boolean isActive) {
		dataSource.open();
		dataSource.updateActive(alarm, isActive);
		dataSource.close();
	}

	//refresh the list
	protected void refresh() {
		dataSource.open();
		alarmList.clear();
		alarmList.addAll(dataSource.getAllAlarms());
		dataSource.close();
		this.notifyDataSetChanged();
	}

	//update checkbox for when items are reused or alarm is deleted
	private void checkboxUpdate(final Alarm alarm, Holder viewHolder) {
		viewHolder.checkbox.setChecked(alarm.isActive());
	}

	//sets up the listView row touchListener
	private View.OnTouchListener rowViewTouchListener(int position, final View view) {
		final Alarm alarm = alarmList.get(position);
		final AlarmProvider alarmProvider = new AlarmProvider(context, alarm);
		final Handler handler = new Handler();
		return new OnSwipeTouchListener(context) {
			//removes the alarm from the list when you swipe right
			@Override
			public void onSwipeRight() {
				//cancel the alarm when deleted
				alarmProvider.cancelAlarm();
				//sets up the animation process
				Animation anim = AnimationUtils.loadAnimation(context,
						android.R.anim.slide_out_right);
				view.startAnimation(anim);
				//delete the alarm from the database
				dataSource.open();
				dataSource.deleteAlarm(alarm);
				// do something after animation finishes
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//refresh the list and close the datasource
						refresh();
						dataSource.close();
					}
				}, anim.getDuration());
			}

			//modifyes the alarm when you click on the row
			@Override
			public void onClick() {
				//cancel the alarm
				alarmProvider.cancelAlarm();
				//sets up the animation process
				Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
				view.startAnimation(anim);
				//get the alarm item from the position
				//build the intent to be run
				final Intent intent = new Intent(context, SetAlarmActivity.class);
				intent.putExtra("alarm", alarm);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// do something after animation finishes
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//start the intent
						context.startActivity(intent);
					}
				}, anim.getDuration());
			}
		};
	}

	private class Holder {
		private TextView alarmTime;
		private TextView description;
		private TextView days;
		private ToggleButton checkbox;

		Holder(View v) {
			alarmTime = (TextView) v.findViewById(R.id.textViewTime);
			description = (TextView) v.findViewById(R.id.textViewDescription);
			days = (TextView) v.findViewById(R.id.textViewDays);
			checkbox = (ToggleButton) v.findViewById(R.id.checkBox);
		}
	}
}
