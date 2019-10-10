package com.RitCapstone.GradingApp.dao;

import java.util.List;

public interface GradingListDAO {

	public List<String> getListOfQuestions(String homework);

	public List<String> getListOfStudents(String homework);

	public List<String> getListOfStudentsForQuestion(String homework, String question);

	public List<String> getListOfQuestionsForStudent(String homework, String username);

}
