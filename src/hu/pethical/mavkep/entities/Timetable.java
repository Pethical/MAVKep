package hu.pethical.mavkep.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Timetable extends Changes {

	private int				change;
	private String			changeAt;
	private List<Changes>	changes;
	private Cost			cost;
	private int				delay;
	private String			dest_real;
	private String			destinationtime;
	private String			distance;
	private String			from;
	private String			get_url;
	private String			map;
	private String			number;
	private String			platform;
	private String			start_real;
	private String			starttime;
	private String			to;
	private String			totaltime;
	private String			type;
	private String			when;
	private String			when_real;

	public int getChange() {
		return this.change;
	}

	public void setChange(int change) {
		this.change = change;
	}

	@Override
	public String getChangeAt() {
		return this.changeAt;
	}

	@Override
	public void setChangeAt(String changeat) {
		this.changeAt = changeat;
	}

	public List<Changes> getChanges() {
		return this.changes;
	}

	public void setChanges(List<Changes> changes) {
		this.changes = changes;
	}

	public Cost getCost() {
		return this.cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	public int getDelay() {
		return this.delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getDest_real() {
		return this.dest_real;
	}

	public void setDest_real(String dest_real) {
		this.dest_real = dest_real;
	}

	public String getDestinationtime() {
		return this.destinationtime;
	}

	public void setDestinationtime(String destinationtime) {
		this.destinationtime = destinationtime;
	}

	public String getDistance() {
		return this.distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
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

	public String getTotaltime() {
		return this.totaltime;
	}

	public void setTotaltime(String totaltime) {
		this.totaltime = totaltime;
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

	public void getDetails() {

	}
}
