package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

/**
 * Created by own on 04.03.2015.
 */
public class EnterText extends Activity {

    boolean first;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_enter_text);
		setTitle("Enter text!");
		//setTitle("ALARM" + ((Alarm) adapter.getItem(pos)).getHour());
		setTheme(R.style.Theme_AppCompat_Dialog);
        setFinishOnTouchOutside(false);
        final MediaPlayer player = MediaPlayer.create(this,R.raw.salesleap);
        player.start();
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
					Intent intent = new Intent(getApplicationContext(),MainActivity.class);
					startActivity(intent);
					player.stop();
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
		this.finishFromChild(this);
	}

	//	private void show(){
//		final Dialog d = new Dialog(this);
//		d.setContentView(R.layout.dialog_enter_text);
//		d.setTitle("Enter text!");
//		d.setCancelable(false);
//		final TextView randomText = (TextView) d.findViewById(R.id.randomTextView);
//		Button confirmButton = (Button) d.findViewById(R.id.enterButton);
//		final EditText inputString = (EditText) d.findViewById(R.id.inputMatcherEditText);
//		final String random = generateRandom();
//		randomText.setText("Text: " + random);
//		confirmButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (inputString.getText().toString().equals(random))
//					d.dismiss();
//				else {
//					Toast.makeText(getApplicationContext(), "Incorrect!", Toast.LENGTH_LONG).show();
//					inputString.setText("");
//				}
//			}
//		});
//		d.show();
//	}
}
