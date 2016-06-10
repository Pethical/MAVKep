package hu.pethical.mavkep.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cost {
	private String	first;
	private String	second;

	public String getFirst() {
		return this.first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getSecond() {
		return this.second;
	}

	public void setSecond(String second) {
		this.second = second;
	}
}
