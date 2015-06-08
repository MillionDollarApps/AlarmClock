package avd.com.alarmclockavd;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class EnterTextService extends Service {
	private Intent intent;

	@Override
	public void onCreate() {
		super.onCreate();
		Intent closeDialog = new Intent(this, EnterText.class);
		if (intent != null) {
			closeDialog.putExtra("id", intent.getExtras().getLong("id"));

		}
		closeDialog.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		closeDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(closeDialog);
		System.out.println("service");
	}


	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		this.intent = intent;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
