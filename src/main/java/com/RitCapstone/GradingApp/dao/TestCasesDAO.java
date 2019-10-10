package com.RitCapstone.GradingApp.dao;

import java.io.File;

public interface TestCasesDAO {

	public boolean getTestCaseFilesToLocal(String homework, String question, String destLocation);

	public boolean createTestCase(String homework, String question, String testCaseNumber, File testcaseInput, File testcaseOutput);

	public boolean updateTestCase(String homework, String question, String testCaseNumber, File testcaseInput, File testcaseOutput);
	
	public boolean testCaseExists(String homework, String question, String testCaseNumber);

	public boolean deleteTestCases(String homework, String question);

}
