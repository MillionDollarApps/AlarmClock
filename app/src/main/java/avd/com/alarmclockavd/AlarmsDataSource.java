package avd.com.alarmclockavd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class AlarmsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private Database dbHelper;
	private String[] allColumns = { Database.COLUMN_ID,
			Database.COLUMN_HOUR, Database.COLUMN_MINUTE, Database.COLUMN_AMPM,
			Database.COLUMN_DAY, Database.COLUMN_ACTIVE, Database.COLUMN_DESCRIPTION,
			Database.COLUMN_RINGTONE, Database.COLUMN_VIBRATE};

	public AlarmsDataSource(Context context) {
		dbHelper = new Database(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Alarm createAlarm(String hour, String minute, String ampm, String days, String active, String description, String ringtone, String vibrate) {
		ContentValues values = new ContentValues();
		values.put(Database.COLUMN_HOUR, hour);
		values.put(Database.COLUMN_MINUTE, minute);
		values.put(Database.COLUMN_AMPM, ampm);
		values.put(Database.COLUMN_DAY, days);
		values.put(Database.COLUMN_ACTIVE, active);
		values.put(Database.COLUMN_DESCRIPTION, description);
		values.put(Database.COLUMN_RINGTONE, ringtone);
		values.put(Database.COLUMN_VIBRATE, vibrate);
		long insertId = database.insert(Database.TABLE_ALARM, null,
				values);
		Cursor cursor = database.query(Database.TABLE_ALARM,
				allColumns, Database.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Alarm newAlarm = cursorToAlarm(cursor);
		cursor.close();
		return newAlarm;
	}

	public void deleteAlarm(Alarm alarm) {
		long id = alarm.getId();
		System.out.println("Alarm deleted with id: " + id);
		database.delete(Database.TABLE_ALARM, Database.COLUMN_ID
				+ " = " + id, null);
	}

	public void update(Alarm alarm, String hour, String minute, String ampm, String days, String description, String ringtone, String vibrate) {
		long id = alarm.getId();
		ContentValues values = new ContentValues();
		values.put(Database.COLUMN_HOUR, hour);
		values.put(Database.COLUMN_MINUTE, minute);
		values.put(Database.COLUMN_AMPM, ampm);
		values.put(Database.COLUMN_DAY, days);
		values.put(Database.COLUMN_DESCRIPTION, description);
		values.put(Database.COLUMN_RINGTONE, ringtone);
		values.put(Database.COLUMN_VIBRATE, vibrate);
		database.update(Database.TABLE_ALARM, values, Database.COLUMN_ID + "=" + id, null);
	}


	public void updateActive(long id, String active) {
		ContentValues values = new ContentValues();
		values.put(Database.COLUMN_ACTIVE, active);
		database.update(Database.TABLE_ALARM, values, Database.COLUMN_ID + "=" + id, null);
	}


	public List<Alarm> getAllAlarms() {
		List<Alarm> alarms = new ArrayList<>();
		Cursor cursor = database.query(Database.TABLE_ALARM,
				allColumns, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Alarm alarm = cursorToAlarm(cursor);
			alarms.add(alarm);
		}
		cursor.close();
		return alarms;
	}

	public Alarm getAlarm(long id) {
		Cursor cursor = database.query(Database.TABLE_ALARM,
				allColumns, Database.COLUMN_ID + "=" + id, null, null, null, null);
		cursor.moveToFirst();
		return cursorToAlarm(cursor);
	}
	private Alarm cursorToAlarm(Cursor cursor) {
		Alarm alarm = new Alarm();
		alarm.setId(cursor.getLong(0));
		alarm.setHour(cursor.getString(1));
		alarm.setMinute(cursor.getString(2));
		alarm.setAmpm(cursor.getString(3));
		alarm.setDays(cursor.getString(4));
		alarm.setActive(cursor.getString(5));
		alarm.setDescription(cursor.getString(6));
		alarm.setRingtone(cursor.getString(7));
		alarm.setVibrate(cursor.getString(8));
		return alarm;
	}
}

