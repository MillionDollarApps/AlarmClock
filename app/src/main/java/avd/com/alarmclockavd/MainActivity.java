package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


public class MainActivity extends Activity {

    private ListView alarmList;
    private ImageView buttonAdd;
    private AlarmsDataSource datasource;
    private AlarmListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //initiating views
        initiateViews();
        datasource = new AlarmsDataSource(this);
        datasource.open();
        adapter = new AlarmListAdapter(getApplicationContext(), datasource.getAllAlarms());
        alarmList.setAdapter (adapter);
        alarmList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (alarmList.getCount() > 10)
                    buttonAdd.setVisibility(View.GONE);
                else
                    buttonAdd.setVisibility(View.VISIBLE);
            }
        });

        buttonAdd.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent (getApplicationContext (), SetAlarmActivity.class);
                startActivity (intent);
            }


        });
    }

    private void initiateViews() {
        alarmList = (ListView) findViewById(R.id.alarmsListView);
        buttonAdd = (ImageView) findViewById(R.id.buttonAdd);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //open database
        datasource.open();
        //refresh alarmList after adding an alarm
        adapter.refreshList(datasource.getAllAlarms());

    }

    @Override
    protected void onPause() {
        super.onPause();
        datasource.close();
    }


}
