package com.RitCapstone.GradingApp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RitCapstone.GradingApp.dao.MarksAndFeedbackDAO;

@Service
public class MarksAndFeedbackService {

	@Autowired
	MarksAndFeedbackDAO marksAndFeedbackDAO;

	private static Logger log = Logger.getLogger(MarksAndFeedbackService.class);

	public boolean saveMarksAndFeedback(String homework, String username, String question, Integer marks,
			String feedback) {

		boolean createMarks = marksAndFeedbackDAO.createMarksAndFeedback(homework, username, question, marks, feedback);

		try {
			if (createMarks) {
				return true;

			} else {
				return marksAndFeedbackDAO.updateMarksAndFeedback(homework, username, question, marks, feedback);

			}
		} catch (Exception e) {
			log.error("Exception in saveMarksAndFeedback: " + e.getMessage());
			return false;
		}
	}

	public Integer getMarks(String homework, String username, String question) {
		return marksAndFeedbackDAO.getMarks(homework, username, question);
	}

	public String getFeedback(String homework, String username, String question) {
		return marksAndFeedbackDAO.getFeedback(homework, username, question);
	}

	public Triple<List<String>, List<List<String>>, List<List<Integer>>> getCompletedGrading(String homework) {

		List<String> students = marksAndFeedbackDAO.getDistinctStudents(homework);
		List<List<String>> questionsForStudent = new ArrayList<>();
		List<List<Integer>> marks = new ArrayList<>();

		for (String student : students) {
			List<String> questions = marksAndFeedbackDAO.getQuestionsForStudent(homework, student);
			Collections.sort(questions);
			questionsForStudent.add(questions);

			List<Integer> marksForQuestion = new ArrayList<>();
			for (String question : questions) {

				int _marks = marksAndFeedbackDAO.getMarks(homework, student, question);
				marksForQuestion.add(_marks);
			}
			marks.add(marksForQuestion);
		}

		return new ImmutableTriple<List<String>, List<List<String>>, List<List<Integer>>>(students, questionsForStudent,
				marks);
	}
}
