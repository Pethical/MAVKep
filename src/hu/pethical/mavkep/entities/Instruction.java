package hu.pethical.mavkep.entities;

import java.util.List;

public class Instruction {
	private List<String>	steps;
	private String			text;

	public List<String> getSteps() {
		return this.steps;
	}

	public void setSteps(List<String> steps) {
		this.steps = steps;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
