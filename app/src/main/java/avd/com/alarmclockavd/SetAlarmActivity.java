package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;


public class SetAlarmActivity extends Activity {

	private WheelView hourWheel;
	private WheelView minuteWheel;
	private ToggleButton ampmButton;
	private AlarmsDataSource dataSource;
	private EditText description;
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
			String minute;
			String hour = (hourWheel.getCurrentItem() + 1) + "";
			String ampm = ampmButton.isChecked() ? "PM" : "AM";
			String days = getDaysOfWeek();
			if (minuteWheel.getCurrentItem() < 10)
				minute = "0" + minuteWheel.getCurrentItem() + "";
			else
				minute = minuteWheel.getCurrentItem() + "";
			dataSource.createAlarm(hour, minute, ampm, days, " ", description.getText().toString());
			Toast.makeText(getApplicationContext(), days, Toast.LENGTH_LONG).show();
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
	}

	private void initiateViews() {
		//instantiating widgets
		hourWheel = (WheelView) findViewById(R.id.hourWheel);
		minuteWheel = (WheelView) findViewById(R.id.minuteWheel);
		ampmButton = (ToggleButton) findViewById(R.id.ampmToggleButton);
		ImageView confirmButton = (ImageView) findViewById(R.id.confirmButton);
		ImageView cancelButton = (ImageView) findViewById(R.id.cancelButton);
		description = (EditText) findViewById(R.id.editTextDescription);
		//initiate and open dataSource
		dataSource = new AlarmsDataSource(getApplicationContext());
		dataSource.open();
		//setting buttons listeners
		confirmButton.setOnClickListener(confirmButtonListener);
		cancelButton.setOnClickListener(cancelButtonListener);
		//setting toggle buttons listeners

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
//		switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
//			case 1 : sun.setChecked(true); break;
//			case 2 : tues.setChecked(true); break;
//			case 3 : mon.setChecked(true); break;
//			case 4 : weds.setChecked(true); break;
//			case 5 : thurs.setChecked(true); break;
//			case 6 : sat.setChecked(true); break;
//			case 7 : fri.setChecked(true); break;
//		}
		if (!(sun.isChecked() && mon.isChecked() && tues.isChecked() && weds.isChecked() && thurs.isChecked() && fri.isChecked() && sat.isChecked()))
			daysOfWeek.append("0");
		daysOfWeek.append(sun.isChecked() ? "1" : "");
		daysOfWeek.append(mon.isChecked() ? "2" : "");
		daysOfWeek.append(tues.isChecked() ? "3" : "");
		daysOfWeek.append(weds.isChecked() ? "4" : "");
		daysOfWeek.append(thurs.isChecked() ? "5" : "");
		daysOfWeek.append(fri.isChecked() ? "6" : "");
		daysOfWeek.append(sat.isChecked() ? "7" : "");
		return daysOfWeek.toString();
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
