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


/**
 * The type Alarm list adapter.
 */
public class AlarmListAdapter extends BaseAdapter {
	private Context context;
	private List<Alarm> alarmList;
	private LayoutInflater layoutInflater;
	private AlarmDataUtils dataUtils;
	private AlarmFunctions alarmFunctions;

	/**
	 * Instantiates a new Alarm list adapter.
	 *
	 * @param context the context
	 */
	public AlarmListAdapter(Context context) {
		this.context = context;
		dataUtils = new AlarmDataUtils(context);
		alarmFunctions = new AlarmFunctions(context);
		alarmList = dataUtils.getAlarmList();
		layoutInflater = LayoutInflater.from(context);
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

	/**
	 * Gets week days representation adapter.
	 *
	 * @param alarm the alarm
	 * @return the week days representation adapter
	 */
	//gets the weekdays string reprezentations to set up the textview from the listview row
    private String buildWeekDaysRepresentation(Alarm alarm) {
	    AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
		return alarm.getDays()==0 ? "Once only" :"Weekly: "+ alarmCalendar.getWeekDaysRepresentation();
    }

	private void setViewHolderItems(int position, Holder viewHolder) {
		//get alarm object from position
		Alarm alarm = alarmList.get(position);
		updateTextViews(viewHolder, alarm);
		updateCheckBox(alarm, viewHolder);
	}

	//update checkbox for when items are reused or alarm is deleted
	private void updateCheckBox(final Alarm alarm, Holder viewHolder) {
		viewHolder.checkbox.setOnCheckedChangeListener(checkBoxListener(alarm));
		viewHolder.checkbox.setChecked(alarm.isActive());
	}

	private void updateTextViews(Holder viewHolder, Alarm alarm) {
		viewHolder.alarmTime.setText(alarm.toString());
		viewHolder.description.setText(alarm.getDescription());
		viewHolder.days.setText(buildWeekDaysRepresentation(alarm));
	}

	//implements the toglebutton(checkbox) listener
	private CompoundButton.OnCheckedChangeListener checkBoxListener(final Alarm alarm) {
		return new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView.isPressed() && isChecked) {
					dataUtils.enableAlarm(alarm);
					alarmFunctions.setAlarm(alarm);
					new AlarmToast(context).show(alarm);
					refresh();
				} else if (buttonView.isPressed()) {
					alarmFunctions.cancelAlarm(alarm);
					dataUtils.disableAlarm(alarm);
					refresh();
				}
			}
		};
	}


	//sets up the listView row touchListener
	private View.OnTouchListener rowViewTouchListener(int position, final View view) {
		final Alarm alarm = alarmList.get(position);
		final Handler handler = new Handler();
		return new OnSwipeTouchListener(context) {
			//removes the alarm from the list when you swipe right
			@Override
			public void onSwipeRight() {
				alarmFunctions.cancelAlarm(alarm);
				Animation anim = AnimationUtils.loadAnimation(context,
						android.R.anim.slide_out_right);
				view.startAnimation(anim);
				dataUtils.deleteAlarm(alarm);
				// do something after animation finishes
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				}, anim.getDuration());
			}

			//modifyes the alarm when you click on the row
			@Override
			public void onClick() {
				//cancel the alarm
				alarmFunctions.cancelAlarm(alarm);
				//sets up the animation process
				Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
				view.startAnimation(anim);
				//build the intent to be run
				final Intent intent = new Intent(context, SetAlarmActivity.class);
				intent.putExtra("alarm", alarm);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
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

		/**
		 * Instantiates a new Holder.
		 *
		 * @param v the v
		 */
		Holder(View v) {
			alarmTime = (TextView) v.findViewById(R.id.textViewTime);
			description = (TextView) v.findViewById(R.id.textViewDescription);
			days = (TextView) v.findViewById(R.id.textViewDays);
			checkbox = (ToggleButton) v.findViewById(R.id.checkBox);
		}
	}

	/**
	 * Refresh the UI AlarmList.
	 */
	public void refresh() {
		alarmList.clear();
		alarmList.addAll(dataUtils.getAlarmList());
		this.notifyDataSetChanged();
	}
}
