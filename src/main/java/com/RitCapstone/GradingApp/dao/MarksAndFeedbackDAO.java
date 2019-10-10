package com.RitCapstone.GradingApp.dao;

import java.util.List;

public interface MarksAndFeedbackDAO {

	public boolean createMarksAndFeedback(String homework, String username, String question, Integer marks,
			String feedback);

	public boolean updateMarksAndFeedback(String homework, String username, String question, Integer marks,
			String feedback);

	public Integer getMarks(String homework, String username, String question);

	public String getFeedback(String homework, String username, String question);

	public List<String> getDistinctStudents(String homework);

	public List<String> getQuestionsForStudent(String homework, String username);
}
