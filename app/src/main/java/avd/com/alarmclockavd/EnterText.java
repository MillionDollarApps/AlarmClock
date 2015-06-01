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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class EnterText extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //receiving the intent that triggered this Activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        AlarmsDataSource dataSource = new AlarmsDataSource(this);
        dataSource.open();
        //audio stream
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        long id = extras.getLong("id");
        Alarm alarm = dataSource.getAlarm(id);
        boolean oneTime = alarm.getDays().charAt(0) == '0';
        //if the alarm is not repeating, disable it after playing once
        if (oneTime) {
            dataSource.updateActive(id, " ");
            new AlarmListAdapter(this, dataSource.getAllAlarms()).cancelAlarm(alarm);
        }
        String uri = alarm.getRingtone();
        final String vibrate = alarm.getVibrate();
        String description = alarm.getDescription();
        String time = alarm.toString();

        dataSource.close();
        //
        //setting up the layout
        setContentView(R.layout.dialog_enter_text);
        //setting up power managers
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        //sets up the title for the dialogbox
        TextView title = (TextView) findViewById(R.id.enterTextTitle);
        if (description.length() > 0) {
            title.setText(time + " - " + description);
        } else {
            title.setText(time);
        }

        //dialogbox cannot be canceled
        setFinishOnTouchOutside(false);

        //setting up MediaPlayer to play the alarm
        final MediaPlayer player = MediaPlayer.create(this, Uri.parse(uri));
        player.setLooping(true);
        player.setVolume(1.0f, 1.0f);
        player.start();

        //Setting up the vibrator
        final Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        startVibrator(vibrate, vibrator);
        //generating and setting the textview
        TextView randomText = (TextView) findViewById(R.id.randomTextView);
        final EditText inputString = (EditText) findViewById(R.id.inputMatcherEditText);
        final String random = generateRandom();
        randomText.setText("Text: " + random);
        //setting a textchangelistener to finish the alarm when the text has been entered, and perform additional operations
        inputString.addTextChangedListener(new TextWatcher() {
            //logic for waiting pausing 3 seconds than starting again if nothing is typed
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!inputString.getText().toString().equals(random)) {
                        player.start();
                        startVibrator(vibrate, vibrator);
                    }
                }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (inputString.getText().toString().equals(random)) {
                    player.stop();
                    player.release();
                    vibrator.cancel();
                    handler.removeCallbacks(runnable);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            default:
                return false;
        }
    }

    private void startVibrator(String vibrate, Vibrator vibrator) {
        if (vibrate.equals("vibrate")) {
            vibrator.vibrate(new long[]{0, 250, 500}, 0);
        }
    }

    private String generateRandom() {
        return randomAlphanumeric(10).replace('I', 'i').replace('l', 'L');
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {

    }
}
