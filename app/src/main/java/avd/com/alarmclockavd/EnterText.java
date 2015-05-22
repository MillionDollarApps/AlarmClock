package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class EnterText extends Activity {
	private PowerManager.WakeLock mWakeLock;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		//receiving the intent that triggered this Activity
		Intent intent = getIntent();
		Bundle extras = intent.getExtras ();
		String uri = extras.getString ("uri");
		String vibrate = extras.getString ("vibrate");
		String description = extras.getString ("description");
		String time = extras.getString ("time");
		//setting up the layout
		setContentView (R.layout.dialog_enter_text);
		//setting up power managers
		getWindow ().addFlags (WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow ().addFlags (WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow ().addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow ().addFlags (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		TextView title = (TextView) findViewById (R.id.enterTextTitle);
		if (description.length () > 0) {
			title.setText (time + " - " + description);
		} else {
			title.setText (time);
		}

		setVolumeControlStream (AudioManager.STREAM_ALARM);
		//cannot be canceled

		setFinishOnTouchOutside (false);
		setTitle (time + " - " + description);

		//setting up MediaPlayer to play the alarm
		final MediaPlayer player = MediaPlayer.create (this, Uri.parse (uri));
		player.setLooping (true);
		player.start ();
		player.setAudioStreamType (AudioManager.STREAM_MUSIC);


		//Setting up the vibrator
		final Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		if (vibrate.equals ("vibrate")) {
			vibrator.vibrate (new long[] {0, 250, 500}, 0);
		}
		//
		TextView randomText = (TextView) findViewById(R.id.randomTextView);
		final EditText inputString = (EditText) findViewById(R.id.inputMatcherEditText);
		final String random = generateRandom();
		randomText.setText("Text: " + random);
		inputString.addTextChangedListener(new TextWatcher() {
			final Handler handler = new Handler ();
			Runnable runnable = new Runnable () {
				@Override
				public void run () {
					if (!inputString.getText ().toString ().equals (random)) {
						player.start ();
					}
				}
			};
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged (final CharSequence s, int start, int before, int count) {
				if (inputString.getText ().toString ().equals (random)) {
					finish ();
					player.stop ();
					player.release ();
					vibrator.cancel ();
					handler.removeCallbacks (runnable);
				} else {
					player.pause ();
					handler.removeCallbacks (runnable);
				}

			}
			@Override
			public void afterTextChanged(Editable s) {
				handler.removeCallbacks (runnable);
				handler.postDelayed (runnable, 3000);
			}
		});


	}

	private String generateRandom(){
		return randomAlphanumeric (10).replace ('I', 'i').replace ('l', 'L');
	}


	@Override
	protected void onPause() {
		super.onPause ();
	}


	@Override
	public void onBackPressed () {

	}
}
