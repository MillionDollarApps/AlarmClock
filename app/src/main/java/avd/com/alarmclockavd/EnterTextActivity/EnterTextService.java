package avd.com.alarmclockavd.EnterTextActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * The type Enter text service.
 */
public class EnterTextService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		Intent closeDialog = new Intent(this, EnterText.class);
		closeDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(closeDialog);
		System.out.println("service");
        onDestroy();
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
