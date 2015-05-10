package avd.com.alarmclockavd;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
        alarmList.setAdapter(adapter);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetAlarmActivity.class);
                startActivity(intent);

//                //Create an offset from the current time in which the alarm will go off.
//                Calendar cal = Calendar.getInstance();
//                cal.set(Calendar.HOUR_OF_DAY, 12);
////                    cal.set(Calendar.AM_PM,0);
//                cal.set(Calendar.MINUTE, 30);
//                cal.set(Calendar.SECOND, 0);
//                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//                long diff = System.currentTimeMillis() - cal.getTimeInMillis();
//                if (diff > 0)
//                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//
//                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//                final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                final AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }


        });
    }

    private void initiateViews() {
        alarmList = (ListView) findViewById(R.id.alarmsListView);
        buttonAdd = (ImageView) findViewById(R.id.buttonAdd);
    }

    @Override
    protected void onResume() {
        //open database
        datasource.open();
        //refresh alarmList after adding an alarm
        adapter.refreshList(datasource.getAllAlarms());
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
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
