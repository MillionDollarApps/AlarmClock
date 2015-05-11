package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class EnterText extends Activity {
	private SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_enter_text);
		setTheme(R.style.Theme_AppCompat_Dialog);
		setTitle("Enter text!");
		//cannot be canceled
		setFinishOnTouchOutside(false);
		//using mediaplayer to play alarm
//		final MediaPlayer player = MediaPlayer.create(this, R.raw.salesleap);
//		player.start();
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
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
	public void onBackPressed() {
	}


	@Override
	protected void onPause() {
		super.onPause();
		finish();
		this.finishFromChild(this);
	}

}
