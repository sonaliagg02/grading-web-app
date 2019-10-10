package com.RitCapstone.GradingApp.service;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RitCapstone.GradingApp.dao.QuestionDAO;

@Service
public class QuestionMetadataService {

	@Autowired
	QuestionDAO questionDAO;

	private static Logger log = Logger.getLogger(QuestionMetadataService.class);

	public Map<?, ?> getMetadata(String homework, String questionNumber) {

		log.debug(
				String.format("Get submission: Homework (%s),question (%s)", homework, questionNumber));

		return questionDAO.getQuestionMetaData(homework, questionNumber);
	}

	public boolean saveMetadata(String homework, String questionNumber, String problemName, String description,
			Date dueDate) {

		// If homework not there then createSubmission
		boolean created = questionDAO.createQuestionMetaData(homework, questionNumber, problemName, description,
				dueDate);

		if (created)
			return created;
		else
			return questionDAO.updateQuestionMetaData(homework, questionNumber, problemName, description, dueDate);

	}

	public String getQuestionNumber(String homeworkNumber, String problemName) {
		return questionDAO.getQuestionNumber(homeworkNumber, problemName);
	}

	public Date getDueDate(String homework) {
		return questionDAO.getDueDate(homework);
	}
}
