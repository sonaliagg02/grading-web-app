package com.RitCapstone.GradingApp;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GradeHomework {

	@NotNull(message = "homework is required")
	private String homework;

	@Min(value = 0, message = "Marks can't be negative")
	@NotNull(message = "Marks not given")
	private Integer marksGiven;

	private String feedback;

	public GradeHomework() {
	}

	public String getHomework() {
		return homework;
	}

	public void setHomework(String homework) {
		this.homework = homework;
	}

	public Integer getMarksGiven() {
		return marksGiven;
	}

	public void setMarksGiven(Integer marksGiven) {
		this.marksGiven = marksGiven;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

}
