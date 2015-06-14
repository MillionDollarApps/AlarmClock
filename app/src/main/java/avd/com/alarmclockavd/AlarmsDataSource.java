package avd.com.alarmclockavd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private Database dbHelper;
	private String[] allColumns = {Database.COLUMN_ID,
			Database.COLUMN_HOUR, Database.COLUMN_MINUTE, Database.COLUMN_AMPM,
			Database.COLUMN_DAY, Database.COLUMN_ACTIVE, Database.COLUMN_DESCRIPTION,
			Database.COLUMN_RINGTONETITLE, Database.COLUMN_RINGTONEURI, Database.COLUMN_VIBRATE,};

	public AlarmsDataSource(Context context) {
		dbHelper = new Database(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Alarm createAlarm(Alarm alarm) {
		ContentValues values = getContentValues(alarm);
		long insertId = database.insert(Database.TABLE_ALARM, null,
				values);
		Cursor cursor = database.query(Database.TABLE_ALARM,
				allColumns, Database.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToNext();
		Alarm newAlarm = cursorToAlarm(cursor);
		cursor.close();
		return newAlarm;
	}

	private Alarm cursorToAlarm(Cursor cursor) {
		return new Alarm.Builder().
				id(cursor.getLong(0)).
				hour(cursor.getInt(1)).
				minute(cursor.getInt(2)).
				ampm(cursor.getInt(3) == 1).
				days(cursor.getInt(4)).
				active(cursor.getInt(5) == 1).
				description(cursor.getString(6)).
				ringtoneTitle(cursor.getString(7)).
				ringtoneUri(cursor.getString(8)).
				vibrate(cursor.getInt(9) == 1).build();
	}

	private ContentValues getContentValues(Alarm alarm) {
		ContentValues values = new ContentValues();
		values.put(Database.COLUMN_HOUR, alarm.getHour());
		values.put(Database.COLUMN_MINUTE, alarm.getMinute());
		values.put(Database.COLUMN_AMPM, alarm.isAmpm() ? 1 : 0);
		values.put(Database.COLUMN_DAY, alarm.getDays());
		values.put(Database.COLUMN_ACTIVE, alarm.isActive() ? 1 : 0);
		values.put(Database.COLUMN_DESCRIPTION, alarm.getDescription());
		values.put(Database.COLUMN_RINGTONETITLE, alarm.getRingtoneTitle());
		values.put(Database.COLUMN_RINGTONEURI, alarm.getRingtoneUri());
		values.put(Database.COLUMN_VIBRATE, alarm.isVibrate() ? 1 : 0);
		return values;
	}

	public void deleteAlarm(Alarm alarm) {
		long id = alarm.getId();
		System.out.println("Alarm deleted with id: " + id);
		database.delete(Database.TABLE_ALARM, Database.COLUMN_ID
				+ " = " + id, null);
	}

	public void updateAlarm(Alarm alarm) {
		ContentValues values = getContentValues(alarm);
		database.update(Database.TABLE_ALARM, values, Database.COLUMN_ID + "=" + alarm.getId(), null);
	}

	public void updateActive(Alarm alarm, boolean isActive) {
		ContentValues values = new ContentValues();
		values.put(Database.COLUMN_ACTIVE, isActive ? 1 : 0);
		database.update(Database.TABLE_ALARM, values, Database.COLUMN_ID + "=" + alarm.getId(), null);
	}

	public List<Alarm> getAllAlarms() {
		List<Alarm> alarms = new ArrayList<>();
		Cursor cursor = database.query(Database.TABLE_ALARM,
				allColumns, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Alarm alarm = cursorToAlarm(cursor);
			alarms.add(alarm);
		}
		Collections.sort(alarms, new TimeComparator());
		cursor.close();
		return alarms;
	}


}

