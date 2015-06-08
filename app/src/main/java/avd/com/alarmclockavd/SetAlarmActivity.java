package avd.com.alarmclockavd;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import static java.lang.Integer.parseInt;


public class SetAlarmActivity extends Activity {

	private WheelView hourWheel;
	private WheelView minuteWheel;
	private ToggleButton ampmButton;
	private EditText description;
	private TextView ringtoneTextView;
	private View view;
	private String[] title;
	private Cursor cursor;
	private ToggleButton vibrate;
	private ToggleButton sun;
	private ToggleButton mon;
	private ToggleButton tues;
	private ToggleButton weds;
	private ToggleButton thurs;
	private ToggleButton fri;
	private ToggleButton sat;
	private Bundle extras;
	private String currentHour;
	private String currentMinute;
	private String currentDays;
	private String currentAMPM;
	private String currentActive;
	private AdView mAdView;
	private ToggleButton repeat;


	//implementating cancelButtonListener
	private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	//implementing confirmButtonListener
	private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//initiate and open dataSource in order to create the alarm
			Alarm alarm = generateAlarm();
			AlarmsDataSource dataSource = new AlarmsDataSource(getApplicationContext());
			dataSource.open();
			AlarmListAdapter adapter = new AlarmListAdapter(getApplicationContext(), dataSource.getAllAlarms());
			if (extras != null) {
				dataSource.updateAlarm(alarm);
			} else {
				dataSource.createAlarm(alarm);
			}
			if (alarm.getActive().equals("active")) {
				adapter.setAlarm(alarm);
			}
			dataSource.close();
			finish();
		}
	};

	private View.OnClickListener ringtoneListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dialogRingtonePicker();
		}
	};

	private Alarm generateAlarm() {
		int minute = minuteWheel.getCurrentItem();
		Alarm alarm = new Alarm();
		if (extras != null) {
			alarm.setId(extras.getLong("id"));
		}
		alarm.setHour((hourWheel.getCurrentItem() + 1) + "");
		alarm.setMinute(minute < 10 ? "0" + minute : minute + "");
		alarm.setAmpm(ampmButton.isChecked() ? "PM" : "AM");
		alarm.setDays(getDaysOfWeek());
		//ugly written condition, because of laziness that checks if the alarm has been modified in order to activate it if it was disable, else.. it will be left alone
		if (extras != null && (!currentMinute.equals(minute < 10 ? "0" + minute : minute + "") || !currentHour.equals(hourWheel.getCurrentItem() + 1 + "") || !currentDays.equals(getDaysOfWeek()) || !currentAMPM.equals(ampmButton.isChecked() ? "PM" : "AM"))) {
			alarm.setActive("active");
			Toast.makeText(getApplicationContext(), "Alarm is set for " + alarm.toString(), Toast.LENGTH_LONG).show();
		} else {
			alarm.setActive(currentActive);
		}
		//default activation of the alarm
		if (extras == null) {
			alarm.setActive("active");
		}
		alarm.setDescription(description.getText().toString());
		alarm.setRingtone(getUri());
		alarm.setTitle(title[0]);
		alarm.setVibrate(vibrate.isChecked() ? "vibrate" : " ");
		return alarm;
	}

	//checks what checkBoxes are checked and returns a string based on it(can be implemented using bitmask)
	private String getDaysOfWeek() {
		StringBuilder daysOfWeek = new StringBuilder();
		daysOfWeek.append(sun.isChecked() ? "1" : "");
		daysOfWeek.append(mon.isChecked() ? "2" : "");
		daysOfWeek.append(tues.isChecked() ? "3" : "");
		daysOfWeek.append(weds.isChecked() ? "4" : "");
		daysOfWeek.append(thurs.isChecked() ? "5" : "");
		daysOfWeek.append(fri.isChecked() ? "6" : "");
		daysOfWeek.append(sat.isChecked() ? "7" : "");
		if (daysOfWeek.length() == 0) {
			daysOfWeek.append("0");
		}
		return daysOfWeek.toString();
	}

	//sets the checkboxes following the intent that triggered this(modifying the alarm)
	private void setDaysOfWeek(String days) {
		ToggleButton[] toggleButtons = {sun, mon, tues, weds, thurs, fri, sat};
		for (int i = 0; i < days.length(); i++) {
			switch (parseInt(days.charAt(i) + "")) {
				case 0:
					for (ToggleButton button : toggleButtons) {
						button.setChecked(false);
					}
					break;
				case 1:
					sun.setChecked(true);
					break;
				case 2:
					mon.setChecked(true);
					break;
				case 3:
					tues.setChecked(true);
					break;
				case 4:
					weds.setChecked(true);
					break;
				case 5:
					thurs.setChecked(true);
					break;
				case 6:
					fri.setChecked(true);
					break;
				case 7:
					sat.setChecked(true);
					break;

			}
		}
	}

	//returns the uri based on the title
	private String getUri() {
		return getRingtones().get(title[0]) == null ? getMusic().get(title[0]) : getRingtones().get(title[0]);
	}

	private LinkedHashMap<String, String> getRingtones() {
		LinkedHashMap<String, String> ringtone = new LinkedHashMap<>();
		RingtoneManager manager = new RingtoneManager(this);
		cursor = manager.getCursor();
		while (cursor.moveToNext()) {
			ringtone.put(cursor.getString(1), cursor.getString(2) + "/" + cursor.getString(0));
		}
		return ringtone;
	}

	private LinkedHashMap<String, String> getMusic() {
		LinkedHashMap<String, String> music = new LinkedHashMap<>();
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = musicResolver.query(musicUri, null, null, null, "title ASC");
		if (cursor != null && cursor.moveToFirst()) {
			int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			do {
				music.put(cursor.getString(titleColumn), musicUri + "/" + cursor.getString(0));
			}
			while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return music;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm);
		//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//initiates the Views of this activity
		mAdView = (AdView) findViewById(R.id.setadView);
		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device. e.g.
		// "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
		AdRequest adRequest = new AdRequest.Builder().build();
		// Start loading the ad in the background.
		mAdView.loadAd(adRequest);
		//initiate views
		initiateViews();
		repeatListeners();
		Intent intent = getIntent();
		extras = intent.getExtras();
		if (extras != null) {
			AlarmsDataSource dataSource = new AlarmsDataSource(getApplicationContext());
			dataSource.open();
			long id = extras.getLong("id");
			Alarm alarm = dataSource.getAlarm(id);
			currentHour = alarm.getHour();
			currentDays = alarm.getDays();
			currentMinute = alarm.getMinute();
			currentAMPM = alarm.getAmpm();
			currentActive = alarm.getActive();
			String currentVibrate = alarm.getVibrate();
			String currentDescription = alarm.getDescription();
			String currentTitle = alarm.getTitle();
			setDaysOfWeek(currentDays);
			setVibrate(currentVibrate);
			setAmpmButton(currentAMPM);
			setTitle(currentTitle);
			description.setText(currentDescription);
			setWheels(parseInt(currentHour) - 1, parseInt(currentMinute));
			dataSource.close();
		} else {
			setWheels(0, 0);
		}
	}

	private void initiateViews() {
		sun = (ToggleButton) findViewById(R.id.toggleSun);
		mon = (ToggleButton) findViewById(R.id.toggleMon);
		tues = (ToggleButton) findViewById(R.id.toggleTues);
		weds = (ToggleButton) findViewById(R.id.toggleWeds);
		thurs = (ToggleButton) findViewById(R.id.toggleThurs);
		sat = (ToggleButton) findViewById(R.id.toggleSat);
		fri = (ToggleButton) findViewById(R.id.toggleFri);
		ampmButton = (ToggleButton) findViewById(R.id.ampmToggleButton);
		ImageView confirmButton = (ImageView) findViewById(R.id.confirmButton);
		ImageView cancelButton = (ImageView) findViewById(R.id.cancelButton);
		description = (EditText) findViewById(R.id.editTextDescription);
		LinearLayout ringtoneLayout = (LinearLayout) findViewById(R.id.ringtoneLayout);
		ringtoneTextView = (TextView) findViewById(R.id.ringtoneTextView);
		title = new String[] {getRingtones().keySet().toArray()[0].toString(), null};
		ringtoneTextView.setText(title[0]);
		vibrate = (ToggleButton) findViewById(R.id.vibrateToogle);
		repeat = (ToggleButton) findViewById(R.id.repeatToggle);
		final LinearLayout daysLayout = (LinearLayout) findViewById(R.id.daysLayout);
		repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					daysLayout.setVisibility(View.VISIBLE);
					if (getDaysOfWeek().equals("0")) {
						setDaysOfWeek("1234567");
					} else {
						setDaysOfWeek(getDaysOfWeek());
					}
				} else {
					daysLayout.setVisibility(View.GONE);
					setDaysOfWeek("0");
				}
			}
		});
		//setting buttons listeners
		confirmButton.setOnClickListener(confirmButtonListener);
		cancelButton.setOnClickListener(cancelButtonListener);
		ringtoneLayout.setOnClickListener(ringtoneListener);
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		setWheels(savedInstanceState.getInt("hour"), savedInstanceState.getInt("minute"));
		setAmpmButton(savedInstanceState.getBoolean("ampm") ? "PM" : "AM");
		setVibrate(savedInstanceState.getBoolean("vibrate") ? "vibrate" : " ");
		description.setText(savedInstanceState.getString("description"));
		setTitle(savedInstanceState.getString("title"));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putInt("hour", (hourWheel.getCurrentItem()));
		outState.putInt("minute", minuteWheel.getCurrentItem());
		outState.putBoolean("vibrate", vibrate.isChecked());
		outState.putBoolean("ampm", ampmButton.isChecked());
		outState.putString("description", description.getText().toString());
		outState.putString("title", title[0]);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		cursor.close();
		super.onDestroy();
	}

	//setting up the wheels(hour and minute)
	private void setWheels(int hour, int minute) {
		hourWheel = (WheelView) findViewById(R.id.hourWheel);
		minuteWheel = (WheelView) findViewById(R.id.minuteWheel);
		hourWheel.setCyclic(true);
		hourWheel.setVisibleItems(4);
		hourWheel.setViewAdapter(new NumericWheelAdapter(this, 1, 12));
		hourWheel.setCurrentItem(hour);
		minuteWheel.setCyclic(true);
		minuteWheel.setVisibleItems(4);
		String[] minutes = new String[60];
		for (int i = 0; i < minutes.length; i++) {
			if (i < 10) {
				minutes[i] = "0" + i;
			} else {
				minutes[i] = i + "";
			}
		}
		minuteWheel.setViewAdapter(new ArrayWheelAdapter<>(getApplicationContext(), minutes));
		minuteWheel.setCurrentItem(minute);
	}

	private void setVibrate(String vibrate) {
		if (vibrate.equals("vibrate")) {
			this.vibrate.setChecked(true);
		} else {
			this.vibrate.setChecked(false);
		}
	}

	private void setAmpmButton(String ampm) {
		if (ampm.equals("AM")) {
			ampmButton.setChecked(false);
		} else {
			ampmButton.setChecked(true);
		}
	}

	private void setTitle(String title) {
		this.title[0] = title;
		ringtoneTextView.setText(this.title[0]);
	}

	private void repeatListeners() {
		final ToggleButton[] toggleButtons = {sun, mon, tues, weds, thurs, fri, sat};
		for (ToggleButton button : toggleButtons) {
			button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int counter = 0;
					for (ToggleButton buttonz : toggleButtons) {
						if (buttonz.isChecked()) {
							counter++;
						}
					}
					if (counter > 0) {
						repeat.setChecked(true);
					} else {
						repeat.setChecked(false);
					}
				}
			});
		}
	}

	//setting up the dialog from where you choose the ringtone
	private void dialogRingtonePicker() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_choose_ringtone);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		final MediaPlayer mp = new MediaPlayer();
		final ImageView confirmButton = (ImageView) dialog.findViewById(R.id.confirmButton);
		confirmButton.setVisibility(View.GONE);
		ImageView cancelButton = (ImageView) dialog.findViewById(R.id.cancelButton);
		//Setting up the listviews;
		final ListView ringtoneListView = (ListView) dialog.findViewById(R.id.ringtoneListView);
		ListView musicListView = (ListView) dialog.findViewById(R.id.musicListView);
		final ArrayList<String> ringtoneList = new ArrayList<>(getRingtones().keySet());
		final ArrayList<String> musicList = new ArrayList<>(getMusic().keySet());
		//intializing adapters
		ArrayAdapter<String> adapterRingtone = new ArrayAdapter<>(this, R.layout.choose_ringtone_row, R.id.rowTextView, ringtoneList);
		ArrayAdapter<String> adapterMusic = new ArrayAdapter<>(this, R.layout.choose_ringtone_row, R.id.rowTextView, musicList);
		//setting up adapters for listviews
		musicListView.setAdapter(adapterMusic);
		ringtoneListView.setAdapter(adapterRingtone);
		//set up itemclicklisteners for listviews
		musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//setting up the selection process for the listview
				if (getView() != null) {
					getView().setSelected(false);
					getView().setBackgroundResource(R.mipmap.back);
					getView().setActivated(false);
				}
				setView(view);
				//setting up the title to be displayed
				title[1] = musicList.get(position);
				//starting media player to play the selected item
				playMediaPlayer(mp);
				confirmButton.setVisibility(View.VISIBLE);
			}
		});

		//setting onItemClickListeners for the ringtoneListView
		ringtoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//setting up the selection process for the listviews
				if (getView() != null) {
					getView().setSelected(false);
					getView().setBackgroundResource(R.mipmap.back);
					getView().setActivated(false);
				}
				setView(view);
				//setting up the title to be displayed
				title[1] = ringtoneList.get(position);
				//starting up media player to play the selected item
				playMediaPlayer(mp);
				confirmButton.setVisibility(View.VISIBLE);
			}
		});

		//set dialog buttons listeners
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				title[0] = title[1];
				ringtoneTextView.setText(title[0]);
				stopMediaPlayer(mp);
				dialog.dismiss();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				title[1] = null;
				stopMediaPlayer(mp);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	//getters and setters to get around static concept
	private View getView() {
		return view;
	}

	private void setView(View view) {
		view.setBackgroundResource(R.drawable.selector_ringtone);
		this.view = view;
	}

	private void playMediaPlayer(MediaPlayer mp) {
		mp.reset();
		try {
			mp.setDataSource(getApplicationContext(), getMediaPlayerUri());
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.setLooping(false);
		mp.start();
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.stop();
			}
		});
	}

	private Uri getMediaPlayerUri() {
		return Uri.parse(getRingtones().get(title[1]) == null ? getMusic().get(title[1]) : getRingtones().get(title[1]));
	}

	private void stopMediaPlayer(MediaPlayer mp) {
		mp.stop();
		mp.release();
	}

}
