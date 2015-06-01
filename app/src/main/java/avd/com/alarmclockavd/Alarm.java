package avd.com.alarmclockavd;


import static java.lang.Integer.parseInt;

public class Alarm {
	private long id;
	private String hour;
	private String minute;
	private String ampm;
	private String active;
	private String days;
	private String description;
	private String ringtone;
	private String vibrate;
	private String title;

	public String getTitle () {
		return title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public String getRingtone() {
		return ringtone;
	}

	public void setRingtone(String ringtone) {
		this.ringtone = ringtone;
	}


	public String getVibrate() {
		return vibrate;
	}

	public void setVibrate(String vibrate) {
		this.vibrate = vibrate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActive(){
		return active;
	}

	public void setActive(String active){
		this.active = active;
	}

	public String getDays(){
		return days;
	}

	public void setDays(String days){
		this.days=days;
	}

	public String getAmpm() {
		return ampm;
	}

	public void setAmpm(String ampm) {
		this.ampm = ampm;
	}
	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute(){
		return minute;
	}

	public void setMinute(String minute){
		this.minute = minute;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return hour + " : " + minute + " " + ampm;
	}


    public int getTime() {
        int hours = parseInt(hour);
        int minutes = parseInt(minute);
        int indicator = ampm.equals("AM") ? 0 : 1;
        int time;
        if (hours == 12 && indicator == 0)
            time = minutes;
        else if (hours < 12 && indicator == 1)
            time = (hours + 12) * 60 + minutes;
        else
            time = hours * 60 + minutes;
        return time;
    }

	public int getHourOfDay () {
		int hours = parseInt (hour);
		int indicator = ampm.equals ("AM") ? 0 : 1;
		int time;
		if (hours == 12 && indicator == 0) {
			time = 0;
		} else if (hours < 12 && indicator == 1) {
			time = (hours + 12);
		} else {
			time = hours;
		}
		return time;
	}

//    private Calendar getDaySpecificCalendar(int day) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay());
//        calendar.set(Calendar.MINUTE, parseInt(alarm.getMinute()));
//        calendar.set(Calendar.SECOND, 0);
//        if (day != 0) {
//            calendar.set(Calendar.DAY_OF_WEEK, day);
//        }
//        if (Calendar.getInstance().getTimeInMillis() > calendar.getTimeInMillis()) {
//            if (day == 0) {
//                calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK) + 1);
//            } else {
//                calendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH) + 1);
//            }
//        }
//        return calendar;
//    }


}

