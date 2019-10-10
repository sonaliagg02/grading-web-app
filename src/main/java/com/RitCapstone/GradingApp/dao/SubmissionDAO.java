package com.RitCapstone.GradingApp.dao;

import java.util.List;

public interface SubmissionDAO {
	public String getSubmissionLocation(String homework, String username, String question);

	public String getSubmissionPath(String homework, String username, String question);

	public boolean createSubmission(String homework, String username, String question, String zipPath, String zipFile);

	public boolean updateSubmission(String homework, String username, String question, String zipPath,
			String zipFileName);

	public boolean addOutputListsAndResults(String homework, String username, String question, List<String> codeOutput,
			List<String> expectedOutput, List<String> codeStatus);

	public boolean updateOutputListsAndResults(String homework, String username, String question,
			List<String> codeOutput, List<String> expectedOutput, List<String> codeStatus);

	public List<String> getCodeOutput(String homework, String username, String question);

	public List<String> getTestcaseResult(String homework, String username, String question);

	public List<String> getExpectedOutput(String homework, String username, String question);

}
