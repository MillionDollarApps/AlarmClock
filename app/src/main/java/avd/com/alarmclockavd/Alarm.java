package avd.com.alarmclockavd;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * The type Alarm.
 */
public class Alarm implements Parcelable {

	/**
	 * The constant CREATOR.
	 */
	public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
		public Alarm createFromParcel(Parcel source) {return new Alarm(source);}

		public Alarm[] newArray(int size) {return new Alarm[size];}
	};
	//
	private final long id;
	private final int hour;
	private final int minute;
	private final boolean ampm;
	private final boolean active;
	private final int days;
	private final String description;
	private final String ringtoneUri;
	private final boolean vibrate;
	private final String ringtoneTitle;

	private Alarm(Builder builder) {
		id = builder.id;
		hour = builder.hour;
		minute = builder.minute;
		ampm = builder.ampm;
		active = builder.active;
		days = builder.days;
		description = builder.description;
		ringtoneUri = builder.ringtoneUri;
		vibrate = builder.vibrate;
		ringtoneTitle = builder.ringtoneTitle;
	}

	/**
	 * Instantiates a new Alarm.
	 *
	 * @param in the in
	 */
	protected Alarm(Parcel in) {
		this.id = in.readLong();
		this.hour = in.readInt();
		this.minute = in.readInt();
		this.ampm = in.readByte() != 0;
		this.active = in.readByte() != 0;
		this.days = in.readInt();
		this.description = in.readString();
		this.ringtoneUri = in.readString();
		this.vibrate = in.readByte() != 0;
		this.ringtoneTitle = in.readString();
	}

	/**
	 * Gets ringtone title.
	 *
	 * @return the ringtone title
	 */
	public String getRingtoneTitle() {
		return ringtoneTitle;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets hour.
	 *
	 * @return the hour
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Gets minute.
	 *
	 * @return the minute
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * Is ampm.
	 *
	 * @return the boolean
	 */
	public boolean isAmpm() {
		return ampm;
	}

	/**
	 * Is active.
	 *
	 * @return the boolean
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Gets days.
	 *
	 * @return the days
	 */
	public int getDays() {
		return days;
	}

	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets ringtone uri.
	 *
	 * @return the ringtone uri
	 */
	public String getRingtoneUri() {
		return ringtoneUri;
	}

	/**
	 * Is vibrate.
	 *
	 * @return the boolean
	 */
	public boolean isVibrate() {
		return vibrate;
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeInt(this.hour);
		dest.writeInt(this.minute);
		dest.writeByte(ampm ? (byte) 1 : (byte) 0);
		dest.writeByte(active ? (byte) 1 : (byte) 0);
		dest.writeInt(this.days);
		dest.writeString(this.description);
		dest.writeString(this.ringtoneUri);
		dest.writeByte(vibrate ? (byte) 1 : (byte) 0);
		dest.writeString(this.ringtoneTitle);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Alarm)) {
			return false;
		}

		Alarm alarm = (Alarm) o;

		if (hour != alarm.hour) {
			return false;
		}
		if (minute != alarm.minute) {
			return false;
		}
		if (ampm != alarm.ampm) {
			return false;
		}
		if (days != alarm.days) {
			return false;
		}
		if (vibrate != alarm.vibrate) {
			return false;
		}
		if (!description.equals(alarm.description)) {
			return false;
		}
		return ringtoneTitle.equals(alarm.ringtoneTitle);

	}

	@Override
	public int hashCode() {
		int result = hour;
		result = 31 * result + minute;
		result = 31 * result + (ampm ? 1 : 0);
		result = 31 * result + days;
		result = 31 * result + description.hashCode();
		result = 31 * result + (vibrate ? 1 : 0);
		result = 31 * result + ringtoneTitle.hashCode();
		return result;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return (hour < 10 ? "0" + hour : hour) + " : " + (minute < 10 ? "0" + minute : minute) + " " +
				(ampm ? "AM" : "PM");
	}

	/**
	 * The type Builder.
	 */
	public static class Builder {
		private long id;
		private int hour;
		private int minute;
		private boolean ampm;
		private boolean active;
		private int days;
		private String description;
		private String ringtoneUri;
		private boolean vibrate;
		private String ringtoneTitle;

		/**
		 * Id builder.
		 *
		 * @param id the id
		 * @return the builder
		 */
		public Builder id(long id) {
			this.id = id;
			return this;
		}

		/**
		 * Hour builder.
		 *
		 * @param hour the hour
		 * @return the builder
		 */
		public Builder hour(int hour) {
			this.hour = hour;
			return this;
		}

		/**
		 * Minute builder.
		 *
		 * @param minute the minute
		 * @return the builder
		 */
		public Builder minute(int minute) {
			this.minute = minute;
			return this;
		}

		/**
		 * Ampm builder.
		 *
		 * @param ampm the ampm
		 * @return the builder
		 */
		public Builder ampm(boolean ampm) {
			this.ampm = ampm;
			return this;
		}

		/**
		 * Active builder.
		 *
		 * @param active the active
		 * @return the builder
		 */
		public Builder active(boolean active) {
			this.active = active;
			return this;
		}

		/**
		 * Days builder.
		 *
		 * @param days the days
		 * @return the builder
		 */
		public Builder days(int days) {
			this.days = days;
			return this;
		}

		/**
		 * Description builder.
		 *
		 * @param description the description
		 * @return the builder
		 */
		public Builder description(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Ringtone uri.
		 *
		 * @param uri the uri
		 * @return the builder
		 */
		public Builder ringtoneUri(String uri) {
			this.ringtoneUri = uri;
			return this;
		}

		/**
		 * Ringtone title.
		 *
		 * @param ringtoneTitle the ringtone title
		 * @return the builder
		 */
		public Builder ringtoneTitle(String ringtoneTitle) {
			this.ringtoneTitle = ringtoneTitle;
			return this;
		}

		/**
		 * Vibrate builder.
		 *
		 * @param vibrate the vibrate
		 * @return the builder
		 */
		public Builder vibrate(boolean vibrate) {
			this.vibrate = vibrate;
			return this;
		}

		/**
		 * Build alarm.
		 *
		 * @return the alarm
		 */
		public Alarm build() {
			return new Alarm(this);
		}
	}
}

