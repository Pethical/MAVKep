package hu.pethical.mavkep.entities;

public class Table {
	private Expected	expected;
	private String		km;
	private String		name;
	private Real		real;
	private Schedule	schedule;

	public Expected getExpected() {
		return this.expected;
	}

	public void setExpected(Expected expected) {
		this.expected = expected;
	}

	public String getKm() {
		return this.km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Real getReal() {
		return this.real;
	}

	public void setReal(Real real) {
		this.real = real;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
}
