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
	private String[] allColumns = { Database.COLUMN_ID,
			Database.COLUMN_HOUR, Database.COLUMN_MINUTE, Database.COLUMN_AMPM,
			Database.COLUMN_DAY, Database.COLUMN_ACTIVE, Database.COLUMN_DESCRIPTION,
			Database.COLUMN_RINGTONE, Database.COLUMN_VIBRATE, Database.COLUMN_TITLE};

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
        ContentValues values = new ContentValues();
        values.put(Database.COLUMN_HOUR, alarm.getHour());
        values.put(Database.COLUMN_MINUTE, alarm.getMinute());
        values.put(Database.COLUMN_AMPM, alarm.getAmpm());
        values.put(Database.COLUMN_DAY, alarm.getDays());
        values.put(Database.COLUMN_ACTIVE, alarm.getActive());
        values.put(Database.COLUMN_DESCRIPTION, alarm.getDescription());
        values.put(Database.COLUMN_RINGTONE, alarm.getRingtone());
        values.put(Database.COLUMN_VIBRATE, alarm.getVibrate());
	    values.put (Database.COLUMN_TITLE, alarm.getTitle ());
	    long insertId = database.insert(Database.TABLE_ALARM, null,
				values);
	    alarm.setId (insertId);
	    return alarm;
    }

	public void deleteAlarm(Alarm alarm) {
		long id = alarm.getId();
		System.out.println ("Alarm deleted with id: " + id);
		database.delete (Database.TABLE_ALARM, Database.COLUMN_ID
				+ " = " + id, null);
    }

	public void updateAlarm (Alarm alarm) {
		long id = alarm.getId();
		ContentValues values = new ContentValues();
        values.put(Database.COLUMN_HOUR, alarm.getHour());
		values.put(Database.COLUMN_MINUTE, alarm.getMinute());
		values.put(Database.COLUMN_AMPM, alarm.getAmpm());
		values.put(Database.COLUMN_DAY, alarm.getDays());
		values.put(Database.COLUMN_ACTIVE, alarm.getActive());
		values.put(Database.COLUMN_DESCRIPTION, alarm.getDescription());
        values.put(Database.COLUMN_RINGTONE, alarm.getRingtone());
        values.put(Database.COLUMN_VIBRATE, alarm.getVibrate());
	    values.put (Database.COLUMN_TITLE, alarm.getTitle ());
	    database.update (Database.TABLE_ALARM, values, Database.COLUMN_ID + "=" + id, null);
	}

	public void updateActive(long id, String active) {
		ContentValues values = new ContentValues();
		values.put (Database.COLUMN_ACTIVE, active);
		database.update (Database.TABLE_ALARM, values, Database.COLUMN_ID + "=" + id, null);
	}
	public List<Alarm> getAllAlarms() {
		List<Alarm> alarms = new ArrayList<>();
		Cursor cursor = database.query(Database.TABLE_ALARM,
				allColumns, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Alarm alarm = cursorToAlarm(cursor);
			alarms.add(alarm);
		}
        Collections.sort (alarms, new TimeComparator ());
        cursor.close ();
        return alarms;
	}

	private Alarm cursorToAlarm (Cursor cursor) {
		Alarm alarm = new Alarm ();
		alarm.setId (cursor.getLong (0));
		alarm.setHour (cursor.getString (1));
		alarm.setMinute (cursor.getString (2));
		alarm.setAmpm (cursor.getString (3));
		alarm.setDays (cursor.getString (4));
		alarm.setActive (cursor.getString (5));
		alarm.setDescription (cursor.getString (6));
		alarm.setRingtone (cursor.getString (7));
		alarm.setVibrate (cursor.getString (8));
		alarm.setTitle (cursor.getString (9));
		return alarm;
	}

	public Alarm getAlarm(long id) {
		Cursor cursor = database.query(Database.TABLE_ALARM,
				allColumns, Database.COLUMN_ID + "=" + id, null, null, null, null);
		cursor.moveToFirst ();
		return cursorToAlarm(cursor);
	}
}

