package hu.pethical.mavkep.entities;

public class TrainInfo {
	private String	delay;
	private String	end;
	private double	latitude;
	private String	line;
	private double	longitude;
	private Number	speed;
	private String	start;
	private String	time;
	private String	train_number;
	private boolean	highlight;

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public String getDelay() {
		return this.delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getEnd() {
		return this.end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Number getSpeed() {
		return this.speed;
	}

	public void setSpeed(Number speed) {
		this.speed = speed;
	}

	public String getStart() {
		return this.start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTrain_number() {
		return this.train_number;
	}

	public void setTrain_number(String train_number) {
		this.train_number = train_number;
	}
}
