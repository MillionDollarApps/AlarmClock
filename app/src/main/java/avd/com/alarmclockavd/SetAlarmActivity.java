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
import android.widget.LinearLayout;
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
	private EditText description;
	private TextView ringtoneTextView;
	private View view;
	private String uri;
    private String[] title;
    private String hour;
    private String minute;
    private String ampm;
    private String days;
    private Cursor cursor;
	private ToggleButton vibrate;



	//implementating cancelButtonListener
	private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish ();
		}
	};
	//implementing confirmButtonListener
	private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            hour = (hourWheel.getCurrentItem() + 1) + "";
            ampm = ampmButton.isChecked() ? "PM" : "AM";
            days = getDaysOfWeek();
            if (minuteWheel.getCurrentItem() < 10)
				minute = "0" + minuteWheel.getCurrentItem() + "";
			else
				minute = minuteWheel.getCurrentItem() + "";
			//initiate and open dataSource in order to create the alarm
			AlarmsDataSource dataSource = new AlarmsDataSource (getApplicationContext ());
			dataSource.open();
            Alarm alarm = setAlarm();
            dataSource.createAlarm(alarm);
            dataSource.close();
            Toast.makeText(getApplicationContext(), days, Toast.LENGTH_LONG).show();
			finish();
		}
	};

	private View.OnClickListener ringtoneListener = new View.OnClickListener () {
		@Override
		public void onClick (View v) {
			dialogRingtonePicker ();
		}
	};

    private Alarm setAlarm() {
        Alarm alarm = new Alarm();
	    alarm.setHour (hour);
	    alarm.setMinute (minute);
	    alarm.setAmpm (ampm);
	    alarm.setDays (days);
	    alarm.setActive (" ");
	    alarm.setDescription (description.getText ().toString ());
	    alarm.setRingtone (getRingtones ().get (title[0]) == null ? getMusic ().get (title[0]) : getRingtones ().get (title[0]));
	    alarm.setTitle (title[0]);
	    alarm.setVibrate (vibrate.isChecked () ? " " : "vibrate");
	    return alarm;
    }

	private LinkedHashMap<String, String> getRingtones () {
		LinkedHashMap<String, String> ringtone = new LinkedHashMap<> ();
		RingtoneManager manager = new RingtoneManager (this);
		cursor = manager.getCursor ();
		while (cursor.moveToNext ()) {
			ringtone.put (cursor.getString (1), cursor.getString (2) + "/" + cursor.getString (0));
		}
		return ringtone;
	}

	private LinkedHashMap<String, String> getMusic () {
		LinkedHashMap<String, String> music = new LinkedHashMap<> ();
		ContentResolver musicResolver = getContentResolver ();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = musicResolver.query (musicUri, null, null, null, "title ASC");
		if (cursor != null && cursor.moveToFirst ()) {
			int titleColumn = cursor.getColumnIndex (android.provider.MediaStore.Audio.Media.TITLE);
			do {
				music.put (cursor.getString (titleColumn), musicUri + "/" + cursor.getString (0));
			}
			while (cursor.moveToNext ());
		}
		if (cursor != null) {
			cursor.close ();
		}
		return music;

	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.set_alarm);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//initiates the Views of this activity
		initiateViews ();
		//setHour, setMinute wheels configuration
		hourWheel.setCyclic (true);
		hourWheel.setVisibleItems (4);
		hourWheel.setViewAdapter (new NumericWheelAdapter (this, 1, 12));
		hourWheel.setCurrentItem (0);
		minuteWheel.setCyclic (true);
		minuteWheel.setVisibleItems (4);
		String[] minutes = new String[60];
		for (int i = 0; i < minutes.length; i++) {
			if (i < 10) {
				minutes[i] = "0" + i;
			} else {
				minutes[i] = i + "";
			}
		}
		minuteWheel.setViewAdapter (new ArrayWheelAdapter<> (getApplicationContext (), minutes));
		minuteWheel.setCurrentItem (0);
	}

	private void initiateViews () {
		title = new String[] {getRingtones ().keySet ().toArray ()[0].toString (), null};
		//instantiating widgets
		hourWheel = (WheelView) findViewById (R.id.hourWheel);
		minuteWheel = (WheelView) findViewById (R.id.minuteWheel);
		ampmButton = (ToggleButton) findViewById (R.id.ampmToggleButton);
		ImageView confirmButton = (ImageView) findViewById (R.id.confirmButton);
		ImageView cancelButton = (ImageView) findViewById (R.id.cancelButton);
		description = (EditText) findViewById (R.id.editTextDescription);
		LinearLayout ringtoneLayout = (LinearLayout) findViewById (R.id.ringtoneLayout);
		ringtoneTextView = (TextView) findViewById (R.id.ringtoneTextView);
		ringtoneTextView.setText (title[0]);
		vibrate = (ToggleButton) findViewById (R.id.vibrateToogle);

		//setting buttons listeners
		confirmButton.setOnClickListener (confirmButtonListener);
		cancelButton.setOnClickListener (cancelButtonListener);
		ringtoneLayout.setOnClickListener (ringtoneListener);
	}

	@Override
	protected void onResume () {
		super.onResume ();
	}

	@Override
	protected void onPause () {
		super.onPause ();

	}

	@Override
	protected void onDestroy () {
		super.onDestroy ();
		cursor.close ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId ();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected (item);
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
        final ImageView confirmButton = (ImageView) dialog.findViewById(R.id.confirmButton);
        confirmButton.setVisibility(View.GONE);
        ImageView cancelButton = (ImageView) dialog.findViewById(R.id.cancelButton);
        //Setting up the listviews;
        final ListView ringtoneListView = (ListView) dialog.findViewById(R.id.ringtoneListView);
		ListView musicListView = (ListView) dialog.findViewById(R.id.musicListView);
		final ArrayList<String> ringtoneList = new ArrayList<>(getRingtones().keySet());
		final ArrayList<String> musicList = new ArrayList<>(getMusic().keySet());

        ArrayAdapter<String> adapterRingtone = new ArrayAdapter<>(this, R.layout.choose_ringtone_row, R.id.rowTextView, ringtoneList);
        ArrayAdapter<String> adapterMusic = new ArrayAdapter<>(this, R.layout.choose_ringtone_row, R.id.rowTextView, musicList);
        //setting up adapters for listviews
        musicListView.setAdapter(adapterMusic);
        //setting adapter for ringtoneListView
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
                //setting up the ringtone to be played
                uri = getMusic().get(musicList.get(position));
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
                //setting up the ringtone to be played
                uri = getRingtones().get(ringtoneList.get(position));
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
	private View getView () {
		return view;
	}

	private void setView (View view) {
		view.setBackgroundResource (R.drawable.selector_ringtone);
		this.view = view;
	}

	private void playMediaPlayer (MediaPlayer mp) {
		mp.reset ();
		try {
			mp.setDataSource (getApplicationContext (), Uri.parse (uri));
			mp.prepare ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
		mp.setLooping (false);
		mp.start ();
		System.out.println (mp.getDuration () * 1000);
		mp.setOnCompletionListener (new MediaPlayer.OnCompletionListener () {
			@Override
			public void onCompletion (MediaPlayer mp) {
				mp.stop ();
			}
		});

	}

	private void stopMediaPlayer (MediaPlayer mp) {
		mp.stop ();
		mp.release ();
	}
}
