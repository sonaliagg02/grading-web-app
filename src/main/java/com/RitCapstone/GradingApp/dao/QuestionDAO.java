package com.RitCapstone.GradingApp.dao;

import java.util.Date;
import java.util.Map;

public interface QuestionDAO {

	public Map<String, Object> getQuestionMetaData(String homework, String questionNumber);

	public boolean createQuestionMetaData(String homework, String questionNumber, String problemName,
			String description, Date dueDate);

	public boolean updateQuestionMetaData(String homework, String questionNumber, String problemName,
			String description, Date dueDate);
	
	public Date getDueDate(String homework);
	
	public String getQuestionNumber(String homeworkNumber, String problemName);
}
