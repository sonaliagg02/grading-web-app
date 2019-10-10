package com.RitCapstone.GradingApp;

import java.util.Map;

public class HomeworkOptions {

	private Map<String, String> homeworkOptions;
	private Map<String, String> questionOptions;

	public HomeworkOptions() {
	}

	@Override
	public String toString() {
		return "HomeworkOptions [homeworkOptions=" + homeworkOptions + ", questionOptions=" + questionOptions + "]";
	}

	public Map<String, String> getHomeworkOptions() {
		return homeworkOptions;
	}

	public void setHomeworkOptions(Map<String, String> homeworkOptions) {
		this.homeworkOptions = homeworkOptions;
	}

	public Map<String, String> getQuestionOptions() {
		return questionOptions;
	}

	public void setQuestionOptions(Map<String, String> questionsOptions) {
		this.questionOptions = questionsOptions;
	}

}
