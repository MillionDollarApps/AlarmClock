package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * The type Enter text.
 */
public class EnterText extends Activity {

	private MediaPlayer player;
	private Vibrator vibrator;
	private boolean service;
	private EditText inputString;
    private Intent closeDialog;
	private Alarm alarm;
	private AudioManager audioManager;
	private TextView randomText;
	private TextView title;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeLayout();
		initializeComponents();
		initialiseViews();
		setViews();
	}

	private void initializeLayout() {
		setContentView(R.layout.dialog_enter_text);
		//setting up power managers
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}

	private void initialiseViews() {
		//dialog title
		title = (TextView) findViewById(R.id.enterTextTitle);
		//random text
		randomText = (TextView) findViewById(R.id.randomTextView);
		//editText input
		inputString = (EditText) findViewById(R.id.inputMatcherEditText);
	}

	private void setViews() {
		//gets the random string
        String random = generateRandom();
		//sets the textView to display the random text
		randomText.setText(random);
		//focus the editText input
		inputString.requestFocus();
		inputString.requestFocusFromTouch();
		//set up the title TextView
		setTitleTextView(title);
		//sets up the listener for the inputString
		inputStringOnTextChangeListener(inputString);
	}

	private void setTitleTextView(TextView title) {
        AlarmCalendar alarmCalendar = new AlarmCalendar(alarm);
		String weekDay = alarmCalendar.getWeekDay();
		String time = alarm.toString();
		String description = alarm.getDescription();
		if (alarm.getDescription().length() == 0) {
			title.setText(weekDay + " " + time);
		} else {
			title.setText(weekDay + " " + time + " - " + description);
		}
	}

	private void inputStringOnTextChangeListener(final EditText inputString) {
		//setting a textchangelistener to finish the alarm when the text has been entered, and perform additional operations
		inputString.addTextChangedListener(new TextWatcher() {
			//logic for waiting pausing 3 seconds than starting again if nothing is typed
			final Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if (!inputString.getText().toString().equals(randomText.getText().toString())) {
						player.start();
						startVibrator(alarm.isVibrate(), vibrator);
					}
				}
			};
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(final CharSequence s, int start, int before, int count) {
				if (inputString.getText().toString().equals(randomText.getText().toString())) {
					handler.removeCallbacks(runnable);
                    cancelOneTimeAlarm();
                    service = false;
                    stopService(closeDialog);
                    player.release();
                    vibrator.cancel();
					finish();
				} else {
					player.pause();
					vibrator.cancel();
					handler.removeCallbacks(runnable);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				handler.removeCallbacks(runnable);
				handler.postDelayed(runnable, 3000);
			}
		});

	}

	private void cancelOneTimeAlarm() {
		AlarmFunctions alarmFunctions = new AlarmFunctions(this);
		AlarmDataUtils dataUtils = new AlarmDataUtils(this);
		if (alarm.getDays() == 0) {
			dataUtils.disableAlarm(alarm);
            alarmFunctions.cancelAlarm(alarm);
        }
	}



	private String generateRandom() {
		return RandomStringUtils.randomAlphanumeric(10).replace('I', 'i').replace('l', 'L');
	}

	private void initializeComponents() {
		//receiving the intent that triggered this Activity
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		//get the alarm that triggered this activity
		alarm = extras.getParcelable("alarm");
		//setting up MediaPlayer to play the alarm
		createMediaPlayer();
		//boolean to know if it's ok to start the service or not, ok by default
		service = true;
		//Setting up the vibrator
		vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		startVibrator(alarm.isVibrate(), vibrator);
		//setting up the audio stream
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
	}

	private void createMediaPlayer() {
		player = MediaPlayer.create(this, Uri.parse(alarm.getRingtoneUri()));
		player.setLooping(true);
		player.setVolume(1.0f, 1.0f);
		player.start();
	}

	//logic for starting the vibrator
	private void startVibrator(boolean vibrate, Vibrator vibrator) {
		if (vibrate) {
			vibrator.vibrate(new long[] {0, 250, 500}, 0);
		}
	}

	@Override
	protected void onResume() {
        super.onResume();
        inputString.requestFocusFromTouch();
		inputString.requestFocus();
        player.start();
        startVibrator(alarm.isVibrate(),vibrator);
	}

	@Override
	protected void onPause() {
        if(player!=null && service) {
            player.pause();
        }
        inputString.clearFocus();
        vibrator.cancel();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// disabling the volume buttons
	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				return true;
			default:
				return false;
		}
	}

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("random",randomText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        randomText.setText(savedInstanceState.getString("random"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
	public void onBackPressed() {
	}

	// logic for tackling the home/recents buttons being pushed
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		closeDialog = new Intent(this, EnterTextService.class);
		closeDialog.putExtra("alarm", alarm);
		if (!hasFocus && service)
			startService(closeDialog);
		else
			stopService(closeDialog);
	}


}
