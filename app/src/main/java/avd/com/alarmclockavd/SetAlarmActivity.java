package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;


public class SetAlarmActivity extends Activity {

	private WheelView hourWheel;
	private WheelView minuteWheel;
	private ToggleButton ampmToogle;
	private EditText description;
	private TextView ringtoneTextView;
	private ToggleButton vibrateToggle;
	private LinearLayout daysLayout;
	private Bundle extras;
	private AdView adView;
	private ToggleButton repeatDaysToggle;
	private ImageView confirmButton;
	private ImageView cancelButton;
	private LinearLayout ringtoneLayout;
	private Alarm alarm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm);
		setAdView();
		//initialises the Views of this activity
		initialiseViews();
		//sets up the listeners for the views
		setUpListeners();
		//get extras if available
		Intent intent = getIntent();
		extras = intent.getExtras();
		if (extras != null) {
			alarm = extras.getParcelable("alarm");
			//sets views because the alarm is modified
			setViews(alarm);
		}

	}

	private void setAdView() {
		adView = (AdView) findViewById(R.id.setadView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	//sets views in case an alarm is received via intent, meaning the alarm will be modified
	private void setViews(Alarm alarm) {
		setWheels(alarm.getHour() - 1, alarm.getMinute());
		ampmToogle.setChecked(!alarm.isAmpm());
		description.setText(alarm.getDescription());
		setDaysOfWeek(alarm.getDays());
		ringtoneTextView.setText(alarm.getRingtoneTitle());
		vibrateToggle.setChecked(alarm.isVibrate());
	}

	private void initialiseViews() {
		//wheelViews
		hourWheel = (WheelView) findViewById(R.id.hourWheel);
		minuteWheel = (WheelView) findViewById(R.id.minuteWheel);
		setWheels(0, 0);
		//ampmToggleButton
		ampmToogle = (ToggleButton) findViewById(R.id.ampmToggleButton);
		//description editText
		description = (EditText) findViewById(R.id.editTextDescription);
		// confirm/cancel buttons
		confirmButton = (ImageView) findViewById(R.id.confirmButton);
		cancelButton = (ImageView) findViewById(R.id.cancelButton);
		// ringtoneLayout - using  the layout so the user can click it
		ringtoneLayout = (LinearLayout) findViewById(R.id.ringtoneLayout);
		// ringtone title is set using this textview
		ringtoneTextView = (TextView) findViewById(R.id.ringtoneTextView);
		if (extras == null) {
			setDefaultRingtoneTitle(ringtoneTextView);
		}
		// vibrate ToggleButton
		vibrateToggle = (ToggleButton) findViewById(R.id.vibrateToggle);
		// repeat ToggleButton
		repeatDaysToggle = (ToggleButton) findViewById(R.id.repeatToggle);
		// layout used to display the days of the weak
		daysLayout = (LinearLayout) findViewById(R.id.daysLayout);
	}

	//sets the default ringtone title(first ringtone from the list)
	private void setDefaultRingtoneTitle(TextView ringtoneTextView) {
		RingtoneManager manager = new RingtoneManager(this);
		Cursor cursor = manager.getCursor();
		cursor.moveToNext();
		ringtoneTextView.setText(cursor.getString(1));
	}

	//setting up the wheels(hour and minute)
	private void setWheels(int hour, int minute) {
		String[] hours = new String[12];
		for (int i = 0; i < hours.length; i++) {
			int z = i + 1;
			hours[i] = "" + (z < 10 ? "0" + z : z);
		}
		String[] minutes = new String[60];
		for (int i = 0; i < minutes.length; i++) {
			minutes[i] = "" + (i < 10 ? "0" + i : i);
		}
		minuteWheel.setCyclic(true);
		minuteWheel.setVisibleItems(4);
		hourWheel.setCyclic(true);
		hourWheel.setVisibleItems(4);
		hourWheel.setViewAdapter(new ArrayWheelAdapter<>(getApplicationContext(), hours));
		hourWheel.setCurrentItem(hour);
		minuteWheel.setViewAdapter(new ArrayWheelAdapter<>(getApplicationContext(), minutes));
		minuteWheel.setCurrentItem(minute);
	}

	private void setUpListeners() {
		//setting buttons listeners
		confirmButtonOnClickListener(confirmButton);
		cancelButtonOnClickListener(cancelButton);
		//set up click listener for the ringtone layout
		ringtoneLayoutOnClickListener(ringtoneLayout);
		//set up check listener for the repeat button
		repeatOnCheckedChangeListener(repeatDaysToggle);
		//set up check listeners for daysOfTheWeek toogle buttons
//		repeaLayoutOnCheckedChildListener(daysLayout, repeatDaysToggle);
	}

	private void cancelButtonOnClickListener(ImageView cancelButton) {
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void confirmButtonOnClickListener(ImageView confirmButton) {
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlarmsDataSource dataSource = new AlarmsDataSource(getApplicationContext());
				dataSource.open();
				Alarm updatedAlarm = getAlarm();
				if (extras != null) {
					dataSource.updateAlarm(alarm.getId(), updatedAlarm);
				} else {
					dataSource.createAlarm(updatedAlarm);
				}
				AlarmProvider alarmProvider = new AlarmProvider(getApplicationContext(), updatedAlarm);
				if (updatedAlarm.isActive()) {
					alarmProvider.setAlarm();
					Toast.makeText(getApplicationContext(), "Alarm is set for " + updatedAlarm.toString
							(), Toast.LENGTH_LONG).show();
					}
				dataSource.close();
				finish();
			}
		});
	}

	//builds the alarm object that gets passed to the database
	private Alarm getAlarm() {
		long id = 0;
		boolean active = true;
		int hour = hourWheel.getCurrentItem() + 1;
		int minute = minuteWheel.getCurrentItem();
		boolean vibrate = vibrateToggle.isChecked();
		boolean ampm = !ampmToogle.isChecked();
		String desc = description.getText().toString();
		String ringtoneTitle = ringtoneTextView.getText().toString();
		RingtonePicker ringtonePicker = new RingtonePicker(this, ringtoneTextView);
		String ringtoneUri = ringtonePicker.getUri();
		int daysOfWeek = repeatDaysToggle.isChecked() ? getDaysOfWeek() : 0;
		if (extras != null) {
			id = alarm.getId();
			active = isActive(new Alarm.Builder().hour(hour).minute(minute)
					.ampm(ampm).days(daysOfWeek).description(desc).vibrate(vibrate).ringtoneTitle
							(ringtoneTitle).build());
		}
		return new Alarm.Builder().id(id).hour(hour).minute(minute).ampm(ampm).days
				(daysOfWeek).active(active).description(desc).vibrate(vibrate).ringtoneTitle(ringtoneTitle)
				.ringtoneUri(ringtoneUri).build();
	}

	//checks if the alarm has been modified
	private boolean isActive(Alarm updatedAlarm) {
		return !updatedAlarm.equals(alarm) || alarm.isActive();
	}

	//checks what daysLayout toggleButtons are checked and returns a string based on it
	private int getDaysOfWeek() {
		int daysOfWeek = 0;
		for (int i = 0; i < daysLayout.getChildCount(); i++) {
			ToggleButton toggleButton = (ToggleButton) daysLayout.getChildAt(i);
			if (toggleButton.isChecked()) {
				daysOfWeek += Math.pow(2, i);
			}
		}
		return daysOfWeek;
	}

	private void setDaysOfWeek(int days) {
		for (int i = 0; i < daysLayout.getChildCount(); i++) {
			ToggleButton toggleButton = (ToggleButton) daysLayout.getChildAt(i);
			if ((days & 1) == 1) {
				toggleButton.setChecked(true);
				repeatDaysToggle.setChecked(true);
			}
			days >>= 1;
		}
	}


	private void ringtoneLayoutOnClickListener(LinearLayout ringtoneLayout) {
		ringtoneLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//initalize ringtonePicker
				RingtonePicker ringtonePicker = new RingtonePicker(SetAlarmActivity.this,
						ringtoneTextView);
				ringtonePicker.show();
			}
		});
	}

	private void repeatOnCheckedChangeListener(ToggleButton repeat) {
		repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					daysLayout.setVisibility(View.VISIBLE);
					if (getDaysOfWeek() == 0) {
						setDaysOfWeek(255);
					} else {
						setDaysOfWeek(getDaysOfWeek());
					}
				} else {
					daysLayout.setVisibility(View.GONE);
				}
			}
		});
	}


	///////////////////////////////////////
	//support for orientation change
	//////////////////////////////////
	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		setWheels(savedInstanceState.getInt("hour"), savedInstanceState.getInt("minute"));
		ampmToogle.setChecked(savedInstanceState.getBoolean("ampm"));
		vibrateToggle.setChecked(savedInstanceState.getBoolean("vibrate"));
		description.setText(savedInstanceState.getString("description"));
		ringtoneTextView.setText(savedInstanceState.getString("title"));
		repeatDaysToggle.setChecked(savedInstanceState.getBoolean("repeat"));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	///////////////////////////////////////
	//support for orientation change
	//////////////////////////////////
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putInt("hour", (hourWheel.getCurrentItem()));
		outState.putInt("minute", minuteWheel.getCurrentItem());
		outState.putBoolean("vibrate", vibrateToggle.isChecked());
		outState.putBoolean("ampm", ampmToogle.isChecked());
		outState.putString("description", description.getText().toString());
		outState.putString("title", ringtoneTextView.getText().toString());
		outState.putBoolean("repeat", repeatDaysToggle.isChecked());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}


}
