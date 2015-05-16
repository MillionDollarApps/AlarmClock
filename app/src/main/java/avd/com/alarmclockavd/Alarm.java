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
}

