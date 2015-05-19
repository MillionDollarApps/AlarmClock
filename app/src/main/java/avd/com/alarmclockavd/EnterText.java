package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class EnterText extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_enter_text);
		setTheme(R.style.Theme_AppCompat_Dialog);
		setTitle("Enter text!");
		//cannot be canceled
		setFinishOnTouchOutside(false);
		Intent intent = getIntent();
		String uri = intent.getStringExtra ("uri");
		String vibrate = intent.getStringExtra ("vibrate");
		//using mediaplayer to play alarm
		final MediaPlayer player = MediaPlayer.create (this, Uri.parse (uri));
		player.start ();
		final Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		if (vibrate.equals ("vibrate")) {
			vibrator.vibrate (Integer.MAX_VALUE);
		}
		TextView randomText = (TextView) findViewById(R.id.randomTextView);
		final EditText inputString = (EditText) findViewById(R.id.inputMatcherEditText);
		final String random = generateRandom();
		randomText.setText("Text: " + random);
		inputString.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (inputString.getText().toString().equals(random)) {
					finish();
					player.stop ();
					player.release ();
					vibrator.cancel();
				}
			}
			@Override
			public void afterTextChanged(Editable s) {

			}
		});


	}

	private String generateRandom(){
		return randomAlphanumeric(10).replace('I','i');
	}


	@Override
	protected void onPause() {
		super.onPause();
//		finish();
//		this.finishFromChild(this);
	}


	@Override
	public void onBackPressed () {

	}
}
