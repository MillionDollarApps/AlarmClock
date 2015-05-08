package avd.com.alarmclockavd;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;


public class SetAlarmActivity extends Activity {

	private WheelView hourWheel;
	private WheelView minuteWheel;
	private WheelView ampmWheel;
	private ImageView confirmButton;
	private ImageView cancelButton;
	private AlarmsDataSource dataSource;
	private EditText description;
	//implementing confirmButtonListener
	private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String minute;
			String hour;
			if (ampmWheel.getCurrentItem() == 0)
				hour = (hourWheel.getCurrentItem() + 1) % 12 + "";
			else
				hour = (hourWheel.getCurrentItem() + 1) + "";
			if (minuteWheel.getCurrentItem() < 10)
				minute = "0" + minuteWheel.getCurrentItem() + "";
			else
				minute = minuteWheel.getCurrentItem() + "";
			dataSource.createAlarm(hour, minute, "3", " ", description.getText().toString());
			Toast.makeText(getApplicationContext(), hour + " : " + minute, Toast.LENGTH_LONG).show();
			finish();
		}
	};
	//implementating cancelButtonListener
	private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm);
		//initiates the Views of this activity
		initiateViews();
		//setHour, setMinute, ampm wheels initiation
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
		ampmWheel.setCyclic(false);
		ampmWheel.setVisibleItems(2);
		String[] ampm = {"AM", "PM"};
		ampmWheel.setViewAdapter(new ArrayWheelAdapter<>(getApplicationContext(), ampm));
		ampmWheel.setCurrentItem(0);
		//setting buttons listeners
		confirmButton.setOnClickListener(confirmButtonListener);
		cancelButton.setOnClickListener(cancelButtonListener);

	}

	@Override
	protected void onResume() {
		dataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dataSource.close();
		super.onPause();
	}

	private void initiateViews() {
		//instantiating widgets
		hourWheel = (WheelView) findViewById(R.id.hourWheel);
		minuteWheel = (WheelView) findViewById(R.id.minuteWheel);
		ampmWheel = (WheelView) findViewById(R.id.ampmWheel);
		confirmButton = (ImageView) findViewById(R.id.confirmButton);
		cancelButton = (ImageView) findViewById(R.id.cancelButton);
		description = (EditText) findViewById(R.id.editTextDescription);
		//initiate and open dataSource
		dataSource = new AlarmsDataSource(getApplicationContext());
		dataSource.open();

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
