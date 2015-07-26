package avd.com.alarmclockavd.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The type Database.
 */
public class Database extends SQLiteOpenHelper {

	/**
	 * The constant TABLE_ALARM.
	 */
	public static final String TABLE_ALARM = "alarm";
	/**
	 * The constant COLUMN_ID.
	 */
	public static final String COLUMN_ID = "_id";
	/**
	 * The constant COLUMN_HOUR.
	 */
	public static final String COLUMN_HOUR = "hour";
	/**
	 * The constant COLUMN_AMPM.
	 */
	public static final String COLUMN_AMPM = "ampm";
	/**
	 * The constant COLUMN_MINUTE.
	 */
	public static final String COLUMN_MINUTE = "minute";
	/**
	 * The constant COLUMN_DAY.
	 */
	public static final String COLUMN_DAY = "days";
	/**
	 * The constant COLUMN_ACTIVE.
	 */
	public static final String COLUMN_ACTIVE = "active";
	/**
	 * The constant COLUMN_DESCRIPTION.
	 */
	public static final String COLUMN_DESCRIPTION = "description";
	/**
	 * The constant COLUMN_RINGTONETITLE.
	 */
	public static final String COLUMN_RINGTONETITLE = "ringtonetitle";
	/**
	 * The constant COLUMN_RINGTONEURI.
	 */
	public static final String COLUMN_RINGTONEURI = "ringtoneuri";
	/**
	 * The constant COLUMN_VIBRATE.
	 */
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

	/**
	 * Instantiates a new Database.
	 *
	 * @param context the context
	 */
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