package avd.com.alarmclockavd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	public static final String TABLE_ALARM = "alarm";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_AMPM = "ampm";
	public static final String COLUMN_MINUTE = "minute";
	public static final String COLUMN_DAY = "days";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_RINGTONETITLE = "ringtonetitle";
	public static final String COLUMN_RINGTONEURI = "ringtoneuri";
	public static final String COLUMN_VIBRATE = "vibrate";

	private static final String DATABASE_NAME = "alarms.db";
	private static final int DATABASE_VERSION = 1;
	// Database creation sql statement
	private static final String DATABASE_CREATE =
			"create table " + TABLE_ALARM + "("
					+ COLUMN_ID + " integer primary key autoincrement, "
					+ COLUMN_HOUR + " integer not null,"
					+ COLUMN_MINUTE + " integer not null,"
					+ COLUMN_AMPM + " integer,"
					+ COLUMN_DAY + " integer not null,"
					+ COLUMN_ACTIVE + " integer not null,"
					+ COLUMN_DESCRIPTION + " text,"
					+ COLUMN_RINGTONETITLE + " text not null,"
					+ COLUMN_RINGTONEURI + " text not null,"
					+ COLUMN_VIBRATE + " text not null);";

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Database.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARM);
		onCreate(db);
	}
}