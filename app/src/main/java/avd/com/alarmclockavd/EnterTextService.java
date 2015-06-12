package avd.com.alarmclockavd;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class EnterTextService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		Intent closeDialog = new Intent(this, EnterText.class);
		closeDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(closeDialog);
		System.out.println("service");
	}

	@Override
	public void onDestroy() {
		System.out.println("service destroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
