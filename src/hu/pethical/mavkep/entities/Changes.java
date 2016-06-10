package hu.pethical.mavkep.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Changes {
	private String	changeAt;
	private int		delay;
	private String	destinationtime;
	private String	from;
	private String	get_url;
	private String	map;
	private String	number;
	private String	platform;
	private String	start_real;
	private String	starttime;
	private String	to;
	private String	type;
	private String	when;
	private String	when_real;

	@JsonIgnore
	public boolean isLocalTransfer() {
		return number.contains("h");
	}

	public int getChange() {
		return 0;
	}

	public String getChangeAt() {
		return this.changeAt;
	}

	public void setChangeAt(String at) {
		this.changeAt = at;
	}

	public int getDelay() {
		return this.delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getDestinationtime() {
		return this.destinationtime;
	}

	public void setDestinationtime(String destinationtime) {
		this.destinationtime = destinationtime;
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getGet_url() {
		return this.get_url;
	}

	public void setGet_url(String get_url) {
		this.get_url = get_url;
	}

	public String getMap() {
		return this.map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPlatform() {
		return this.platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getStart_real() {
		return this.start_real;
	}

	public void setStart_real(String start_real) {
		this.start_real = start_real;
	}

	public String getStarttime() {
		return this.starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWhen() {
		return this.when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public String getWhen_real() {
		return this.when_real;
	}

	public void setWhen_real(String when_real) {
		this.when_real = when_real;
	}
}
