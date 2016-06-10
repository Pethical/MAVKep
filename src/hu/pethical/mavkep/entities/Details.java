package hu.pethical.mavkep.entities;

import java.io.IOException;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Details {
	private List<Table>	table;
	private String		time;

	public static Details CreateFromString(String content) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(content, Details.class);
	}

	public List<Table> getTable() {
		return this.table;
	}

	public void setTable(List<Table> table) {
		this.table = table;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
