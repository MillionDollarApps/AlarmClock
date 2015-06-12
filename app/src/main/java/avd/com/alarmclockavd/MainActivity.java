package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;


public class MainActivity extends Activity {

	private ImageView buttonAdd;
	private AlarmsDataSource datasource;
	private AdView adView;
	private AlarmListAdapter adapter;
	private List<Alarm> alarmList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//initiate datasource
		datasource = new AlarmsDataSource(this);
		alarmList = getAlarmList();
		//initiating views
		initiateViews();
		//set adView
		setAdView();
	}

	//initializing views
	private void initiateViews() {
		//initialize alarmListView
		ListView alarmListView = (ListView) findViewById(R.id.alarmsListView);
		//initialize the add button
		buttonAdd = (ImageView) findViewById(R.id.buttonAdd);
		//initialize the adView
		adView = (AdView) findViewById(R.id.adView);
		//set up click listener for add button
		clickListenerButtonAdd(buttonAdd);
		//set up the adapter for the alarmListView
		adapter = getAdapter(alarmList);
		alarmListView.setAdapter(adapter);
		//set up layout change listener for alarmListView in order to limit the alarms to 10 by
		// hiding the add button
		layoutChangeListener(alarmListView);
	}

	//retrieves the adapter
	private AlarmListAdapter getAdapter(List<Alarm> alarmList) {
		return new AlarmListAdapter(this, alarmList);
	}

	//manage add button visibility depending on the number of alarms
	private void layoutChangeListener(final ListView alarmListView) {
		alarmListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (alarmListView.getCount() > 10) {
					buttonAdd.setVisibility(View.GONE);
				} else {
					buttonAdd.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void clickListenerButtonAdd(ImageView buttonAdd) {
		buttonAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SetAlarmActivity.class);
				startActivity(intent);
			}
		});
	}

	//retrieves all alarms from the dataSource and adds them to a list
	private List<Alarm> getAlarmList() {
		datasource.open();
		List<Alarm> alarmList = datasource.getAllAlarms();
		datasource.close();
		return alarmList;
	}

	private void setAdView() {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
		//refresh the alarmlist
		adapter.refresh();
	}

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}


}
