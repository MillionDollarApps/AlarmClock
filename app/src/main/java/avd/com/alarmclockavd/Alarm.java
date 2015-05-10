package avd.com.alarmclockavd;


public class Alarm {
	private long id;
	private String hour;
	private String minute;
	private String ampm;
	private String active;
	private String days;
	private String description;

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

}

