package hu.pethical.mavkep.entities;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeTableDataSource {

	private String			date;
	private String			route;
	private List<Timetable>	timetable;

	public static TimeTableDataSource createFromString(String string) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(string, TimeTableDataSource.class);
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRoute() {
		return this.route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public List<Timetable> getTimetable() {
		return this.timetable;
	}

	public void setTimetable(List<Timetable> timetable) {
		this.timetable = timetable;
	}
}
