package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;


/**
 * The type Main activity.
 */
public class MainActivity extends Activity {

	private ImageView buttonAdd;
	private AdView adView;
	private AlarmListAdapter adapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initiateViews();
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
		adapter = new AlarmListAdapter(this);
		alarmListView.setAdapter(adapter);
		//set up layout change listener for alarmListView in order to limit the alarms to 10 by
		// hiding the add button
		layoutChangeListener(alarmListView);
	}



	//manage add button visibility depending on the number of alarms
	private void layoutChangeListener(final ListView alarmListView) {

		alarmListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				buttonAdd.setVisibility(View.VISIBLE);
				if (alarmListView.getCount() > 10)
					buttonAdd.setVisibility(View.GONE);

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
