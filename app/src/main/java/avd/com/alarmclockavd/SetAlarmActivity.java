package avd.com.alarmclockavd;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;


public class SetAlarmActivity extends Activity {

	private WheelView hourWheel;
	private WheelView minuteWheel;
	private ToggleButton ampmButton;
	private AlarmsDataSource dataSource;
	private EditText description;
	private TextView ringtoneTextView;
	private View view;
	private String uri;
	private String title;
	private String vibrate;


	//implementating cancelButtonListener
	private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
//			finish();
			dialogRingtonePicker();
		}
	};
	//implementing confirmButtonListener
	private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String minute;
			String hour = (hourWheel.getCurrentItem() + 1) + "";
			String ampm = ampmButton.isChecked() ? "PM" : "AM";
			String days = getDaysOfWeek();
			if (minuteWheel.getCurrentItem() < 10)
				minute = "0" + minuteWheel.getCurrentItem() + "";
			else
				minute = minuteWheel.getCurrentItem() + "";
			//initiate and open dataSource in order to create the alarm
			dataSource = new AlarmsDataSource(getApplicationContext());
			dataSource.open();
			dataSource.createAlarm(hour, minute, ampm, days, " ", description.getText().toString(), uri == null ? " " : uri, vibrate == null ? " " : vibrate);
			Toast.makeText(getApplicationContext(), days, Toast.LENGTH_LONG).show();
			dataSource.close();
			finish();
		}
	};

	//getters and setters to get around static concept
	private View getView() {
		return view;
	}

	private void setView(View view) {
		view.setBackgroundResource(R.drawable.selector_ringtone);
		this.view = view;
	}


	private void setUri(String uri) {
		this.uri = uri;
	}

	private void playMediaPlayer(MediaPlayer mp) {
		mp.reset();
		try {
			mp.setDataSource(getApplicationContext(), Uri.parse(uri));
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.setLooping(false);
		mp.start();
		System.out.println(mp.getDuration() * 1000);
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.stop();
			}
		});

	}

	private void stopMediaPlayer(MediaPlayer mp) {
		mp.stop();
		mp.release();
	}


	private void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//initiates the Views of this activity
		initiateViews();
		//setHour, setMinute wheels configuration
		hourWheel.setCyclic(true);
		hourWheel.setVisibleItems(4);
		hourWheel.setViewAdapter(new NumericWheelAdapter(this, 1, 12));
		hourWheel.setCurrentItem(0);
		minuteWheel.setCyclic(true);
		minuteWheel.setVisibleItems(4);
		String[] minutes = new String[60];
		for (int i = 0; i < minutes.length; i++)
			if (i < 10)
				minutes[i] = "0" + i;
			else
				minutes[i] = i + "";
		minuteWheel.setViewAdapter(new ArrayWheelAdapter<>(getApplicationContext(), minutes));
		minuteWheel.setCurrentItem(0);
	}

	private void initiateViews() {
		//instantiating widgets
		hourWheel = (WheelView) findViewById(R.id.hourWheel);
		minuteWheel = (WheelView) findViewById(R.id.minuteWheel);
		ampmButton = (ToggleButton) findViewById(R.id.ampmToggleButton);
		ImageView confirmButton = (ImageView) findViewById(R.id.confirmButton);
		ImageView cancelButton = (ImageView) findViewById(R.id.cancelButton);
		description = (EditText) findViewById(R.id.editTextDescription);
		ringtoneTextView = (TextView) findViewById(R.id.ringtoneTextView);

		//setting buttons listeners
		confirmButton.setOnClickListener(confirmButtonListener);
		cancelButton.setOnClickListener(cancelButtonListener);

	}

	private String getDaysOfWeek() {
		StringBuilder daysOfWeek = new StringBuilder();
		ToggleButton sun = (ToggleButton) findViewById(R.id.toggleSun);
		ToggleButton tues = (ToggleButton) findViewById(R.id.toggleTues);
		ToggleButton mon = (ToggleButton) findViewById(R.id.toggleMon);
		ToggleButton weds = (ToggleButton) findViewById(R.id.toggleWeds);
		ToggleButton thurs = (ToggleButton) findViewById(R.id.toggleThurs);
		ToggleButton sat = (ToggleButton) findViewById(R.id.toggleSat);
		ToggleButton fri = (ToggleButton) findViewById(R.id.toggleFri);
		daysOfWeek.append(sun.isChecked() ? "1" : "");
		daysOfWeek.append(mon.isChecked() ? "2" : "");
		daysOfWeek.append(tues.isChecked() ? "3" : "");
		daysOfWeek.append(weds.isChecked() ? "4" : "");
		daysOfWeek.append(thurs.isChecked() ? "5" : "");
		daysOfWeek.append(fri.isChecked() ? "6" : "");
		daysOfWeek.append(sat.isChecked() ? "7" : "");
		if (daysOfWeek.length() == 0)
			daysOfWeek.append("0");
		return daysOfWeek.toString();
	}

	private void dialogRingtonePicker() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_choose_ringtone);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		final MediaPlayer mp = new MediaPlayer();
		ImageView confirmButton = (ImageView) dialog.findViewById(R.id.confirmButton);
		ImageView cancelButton = (ImageView) dialog.findViewById(R.id.cancelButton);
		final ListView ringtoneListView = (ListView) dialog.findViewById(R.id.ringtoneListView);
		ListView musicListView = (ListView) dialog.findViewById(R.id.musicListView);
		final ArrayList<String> ringtoneList = new ArrayList<>(getRingtones().keySet());
		final ArrayList<String> musicList = new ArrayList<>(getMusic().keySet());
		ArrayAdapter<String> adapterRingtone = new ArrayAdapter<>(this, R.layout.choose_ringtone_row, R.id.rowTextView, ringtoneList);
		ArrayAdapter<String> adapterMusic;
		if (musicList.size() == 0) {
			adapterMusic = new ArrayAdapter<>(this, R.layout.choose_music_row, R.id.musicRowText, musicList);
			musicListView.setAdapter(adapterMusic);
			musicListView.setFocusable(false);
			musicListView.setFocusableInTouchMode(false);
			musicListView.setItemsCanFocus(false);
			musicListView.setClickable(false);
		} else {
			adapterMusic = new ArrayAdapter<>(this, R.layout.choose_ringtone_row, R.id.rowTextView, musicList);
			//setting up adapters for listviews
			musicListView.setAdapter(adapterMusic);
			ringtoneListView.setAdapter(adapterRingtone);
			//set up itemclicklisteners for listviews
			musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (getView() != null) {
						getView().setSelected(false);
						getView().setBackgroundResource(R.mipmap.back);
						getView().setActivated(false);
					}
					setView(view);
					String uri = getMusic().get(musicList.get(position));
					setTitle(musicList.get(position));
					setUri(uri);
					playMediaPlayer(mp);
				}
			});
		}
		ringtoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (getView() != null) {
					getView().setSelected(false);
					getView().setBackgroundResource(R.mipmap.back);
					getView().setActivated(false);
				}
				setView(view);
				String uri = getRingtones().get(ringtoneList.get(position));
				setTitle(ringtoneList.get(position));
				setUri(uri);
				playMediaPlayer(mp);
			}
		});

		//set dialog buttons listeners
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopMediaPlayer(mp);
				ringtoneTextView.setText(title);
				dialog.dismiss();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopMediaPlayer(mp);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private LinkedHashMap<String, String> getRingtones() {
		LinkedHashMap<String, String> ringtone = new LinkedHashMap<>();
		RingtoneManager manager = new RingtoneManager(this);
		Cursor cursor = manager.getCursor();
		while (cursor.moveToNext()) {
			ringtone.put(cursor.getString(1), cursor.getString(2) + "/" + cursor.getString(0));
		}
		cursor.close();
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
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
