package com.RitCapstone.GradingApp;

import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Homework {

	@NotBlank(message = "Homework name is required")
	private String id;

	@Min(value = 1, message = "Number of questions should be greater than zero")
	private Integer numberOfQuestions;

	@NotNull(message = "Due date is required")
	private Date dueDate;

	private Question[] questions;

	public Homework() {
	}

	public void addQuestion(Question q) {

		if (this.questions == null) {
			this.questions = new Question[1];
			this.questions[0] = q;

		} else {
			int newLength = this.questions.length + 1;
			this.questions = Arrays.copyOf(this.questions, newLength);
			this.questions[newLength - 1] = q;
		}

	}

	@Override
	public String toString() {
		return "Homework [id=" + id + ", numberOfQuestions=" + numberOfQuestions + ", dueDate=" + dueDate
				+ ", questions=" + Arrays.toString(questions) + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(Integer numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Question[] getQuestions() {
		return questions;
	}

	public void setQuestions(Question[] questions) {
		this.questions = questions;
	}

}
