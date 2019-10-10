package com.RitCapstone.GradingApp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RitCapstone.GradingApp.dao.SubmissionDAO;

@Service
@Transactional
public class SubmissionDBService {

	@Autowired
	SubmissionDAO submissionDAO;

//	SubmissionDAO submissionDAO = new SubmissionDAOImpl(); // use this for testing individual class

	private static Logger log = Logger.getLogger(SubmissionDBService.class);

	public String getSubmissionLocation(String homework, String username, String question) {
		return submissionDAO.getSubmissionLocation(homework, username, question);
	}

	public String getSubmissionPath(String homework, String username, String question) {
		return submissionDAO.getSubmissionPath(homework, username, question);
	}

	public List<String> getCodeOutput(String homework, String username, String question) {
		return submissionDAO.getCodeOutput(homework, username, question);
	}

	public List<String> getExpectedOutput(String homework, String username, String question) {
		return submissionDAO.getExpectedOutput(homework, username, question);
	}

	public List<String> getTestcaseResult(String homework, String username, String question) {
		return submissionDAO.getTestcaseResult(homework, username, question);
	}

	/**
	 * Method to create a new submission. This happens when the student submits the
	 * homework for the first time
	 * 
	 * If the submission is the first one for the homework, we create a new
	 * collection
	 * 
	 * @param homework homework number of the solution
	 * @param username username of student submitting the solution
	 * @param question question number of the solution
	 */
	public boolean saveSubmission(String homework, String username, String question, String zipPath,
			String zipFileName) {

		// If homework not there then createSubmission
		if (getSubmissionLocation(homework, username, question) == null) {

			log.debug(String.format("Creating new submission: Homework (%s), username (%s), question (%s)", homework,
					username, question));
			return submissionDAO.createSubmission(homework, username, question, zipPath, zipFileName);
		}

		// If homework already there then updateSubmission
		else {
			log.debug(String.format("Updatinng submission: Homework (%s), username (%s), question (%s)", homework,
					username, question));
			return submissionDAO.updateSubmission(homework, username, question, zipPath, zipFileName);
		}

	}

	public boolean saveOutputsAndResults(String homework, String username, String question, List<String> codeOutput,
			List<String> expectedOutput, List<String> codeStatus) {

		try {
			boolean tryAdd = submissionDAO.addOutputListsAndResults(homework, username, question, codeOutput,
					expectedOutput, codeStatus);

			if (!tryAdd) {
				boolean tryUpdate = submissionDAO.updateOutputListsAndResults(homework, username, question, codeOutput,
						expectedOutput, codeStatus);
				return tryUpdate;
			} else {
				return tryAdd;
			}

		} catch (Exception e) {
			log.error("Error in saveOutputsAndResults:" + e.getMessage());
			return false;
		}
	}

	public static void main(String[] args) {
//		new SubmissionDBService().saveOutputAndTestCasesOutput("Hw1", "test2@rit.edu", "2", new ArrayList<>(), new ArrayList<>());
//		new SubmissionDBService().saveOutputAndTestCasesOutput("Hw1", "test2@rit.edu", "2", null, null);
	}
}
