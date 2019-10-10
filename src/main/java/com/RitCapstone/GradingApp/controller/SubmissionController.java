package com.RitCapstone.GradingApp.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.RitCapstone.GradingApp.HomeworkOptions;
import com.RitCapstone.GradingApp.Submission;
import com.RitCapstone.GradingApp.service.FileService;
import com.RitCapstone.GradingApp.service.HomeworkOptionsService;
import com.RitCapstone.GradingApp.service.OnlineCompileAPIService;
import com.RitCapstone.GradingApp.service.QuestionMetadataService;
import com.RitCapstone.GradingApp.service.SubmissionDBService;
import com.RitCapstone.GradingApp.service.TestCaseDBService;
import com.RitCapstone.GradingApp.validator.SubmissionValidator;

@Controller
@RequestMapping("/student")
@SessionAttributes(value = { "submission", "submissionConfirmationMap" })
public class SubmissionController {

	private static final String PASS = "Passed";
	private static final String FAIL = "Failed";

	@Autowired
	SubmissionValidator submissionValidator;

	@Autowired
	FileService fileService;

	@Autowired
	SubmissionDBService submissionDBService;

	@Autowired
	TestCaseDBService testCaseDBService;

	@Autowired
	OnlineCompileAPIService compileAPIService;

	@Autowired
	HomeworkOptionsService homeworkOptionsService;

	@Autowired
	QuestionMetadataService questionMetadataService;

	private static Logger log = Logger.getLogger(SubmissionController.class);

	/**
	 * This method will trim all the strings received from form data
	 */
	@InitBinder
	public void stringTrimmer(WebDataBinder dataBinder) {

		StringTrimmerEditor trimmer = new StringTrimmerEditor(true);
		// If after trimming the string is empty, it is converted to null {Set by true}

		dataBinder.registerCustomEditor(String.class, trimmer);
	}

	/**
	 * This method displays the initial form
	 * 
	 * @param model Initially model will be empty.If there are validation problems,
	 *              the model will consist of submission, bindingResult. This is due
	 *              to RedirectAttributes
	 * @return .jsp file to display
	 */
	@GetMapping("/showForm")
	public String showForm(Model model) {
		String log_prepend = "[GET /showForm]";
		String jspToDisplay = "student-submission";

		// It can contain submission attribute that was added by redirectAttribute
		if (!model.containsAttribute("submission")) {
			model.addAttribute("submission", new Submission());
			log.debug(log_prepend + "adding submission to model");
		}

		if (!model.containsAttribute("hw")) {

			HomeworkOptions hwOptions = new HomeworkOptions();
			hwOptions.setHomeworkOptions(homeworkOptionsService.getHomeworkOptions());
			model.addAttribute("hw", hwOptions);

			log.debug(log_prepend + "adding hw to model");
		}

		log.debug(log_prepend + "model: " + model);
		log.debug(log_prepend + "Displaying " + jspToDisplay);

		return jspToDisplay;
	}

	/**
	 * This method handles the POST method for submission
	 * 
	 * @param submission         Model Attribute from Submission class
	 * @param bindingResult      Shows if there are errors
	 * @param redirectAttributes Adds attributes if there are validation errors
	 * @return page to be displayed, if there are validation errors, showForm is
	 *         displayed else showForm2 is displayed
	 */
	@PostMapping("/showForm")
	public String validateShowForm(@Valid @ModelAttribute("submission") Submission submission,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		String log_prepend = "[POST /showForm]";

		// we want to focus on errors on homework and username and date field

		List<FieldError> homework_err = bindingResult.getFieldErrors("homework");
		List<FieldError> username_err = bindingResult.getFieldErrors("username");

		if (homework_err.size() + username_err.size() > 0) {
			redirectAttributes.addFlashAttribute("submission", submission);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);

			log.debug(log_prepend + "Redirecting to showForm");
			return "redirect:showForm";
		}

		Date currentDate = new Date();
		submission.setDate(currentDate);

		Date dueDate = questionMetadataService.getDueDate(submission.getHomework());

		if (dueDate.compareTo(currentDate) < 0) {
			// dueDate is before currentDate
			bindingResult.rejectValue("date", "lateSubmission",
					"Sorry! you are late to submit your Homework! Due date: " + dueDate);
		}

		List<FieldError> date_err = bindingResult.getFieldErrors("date");
		if (date_err.size() > 0) {
			redirectAttributes.addFlashAttribute("submission", submission);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);

			log.debug(log_prepend + "Redirecting to showForm");
			return "redirect:showForm";
		}

		log.debug(log_prepend + "Redirecting to showForm2");
		return "redirect:showForm2";
	}

	/**
	 * Method to handle remaining form, This form consists of question to select
	 * 
	 * @return .jsp file to display
	 */
	@GetMapping("/showForm2")
	public String showRemainingForm(@SessionAttribute("submission") Submission submission, Model model) {

		String log_prepend = "[GET /showForm2]";

		HomeworkOptions hwOptions = new HomeworkOptions();
		hwOptions.setHomeworkOptions(homeworkOptionsService.getHomeworkOptions());
		hwOptions.setQuestionOptions(homeworkOptionsService.getQuestionNameOptions(submission.getHomework()));
		model.addAttribute("hw", hwOptions);

		Map<?, ?> map = homeworkOptionsService.getFileRestrictionInfo();

		model.addAttribute("langaugeOptions", map.get("langaugeOptions"));
		model.addAttribute("writeupExtension", map.get("writeupExtension"));
		model.addAttribute("codeExtension", map.get("codeExtension"));

		log.debug(log_prepend + "Model:" + model);
		String jspToDisplay = "student-submission-remaining";
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		return jspToDisplay;

	}

	/**
	 * This method handles the POST method for submission (Part 2)
	 * 
	 * @param submission         Model Attribute from Submission class
	 * @param bindingResult      Shows if there are errors
	 * @param redirectAttributes Adds attributes if there are validation errors
	 * @return page to be displayed, if there are validation errors, showForm2 is
	 *         displayed else showConfirmation is displayed
	 */
	@PostMapping("/showForm2")
	public String validateRemainingForm(@Valid @ModelAttribute("submission") Submission submission,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

		String log_prepend = "[POST /showForm2]";
		submissionValidator.validate(submission, bindingResult);
		log.debug(log_prepend + "Model:" + model);
		log.debug(log_prepend + "Submission:" + submission);
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("submission", submission);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);

			log.debug(log_prepend + "Redirecting to showForm2");
			return "redirect:showForm2";
		}

		log.debug(log_prepend + "Redirecting to showConfirmation");
		return "redirect:pleaseWait";
	}

	@GetMapping("/pleaseWait")
	public String displayWait() {
		// This is display the loader

		// while displaying the loader if user forcefully stops the processing via
		// browser,
		// the loader continue to display
		return "please-wait";
	}

	/**
	 * Method to display confirmation and list of files submitted Also, files are
	 * saved on the machine
	 * 
	 * @param submission Session Attribute
	 * @return jsp file to display
	 */
	@GetMapping("/runStudentCode")
	public String saveAndRunStudentCode(@SessionAttribute("submission") Submission submission, Model model) {

		String log_prepend = "[GET /runStudentCode]";
		String homework = submission.getHomework();
		String username = submission.getUsername();
		String problemName = submission.getProblemName();
		String language = submission.getLanguage();

		String questionNumber = questionMetadataService.getQuestionNumber(homework, problemName);

		// save the files uploaded my student to local machine
		String zipPath = fileService.saveStudentSubmission(homework, username, questionNumber,
				submission.getCodeFiles(), submission.getWriteupFiles());

		// save homework, question, username, and submission location to mongoDB
		boolean savedSubmission = submissionDBService.saveSubmission(homework, username, questionNumber, zipPath,
				questionNumber + ".zip");
		if (!savedSubmission) {
			log.error(String.format("%s Submission not saved:Homework (%s), username (%s), question (%s)", log_prepend,
					homework, username, questionNumber));
		}

		// Unzip the test case files to local
		String unzipTestCaseLoc = zipPath + "testCases" + questionNumber;
		boolean testcaseToLocal = testCaseDBService.getTestCases(homework, questionNumber, unzipTestCaseLoc);
		if (!testcaseToLocal) {
			log.error(String.format("%s Test case not found :Homework (%s), question (%s)", log_prepend, homework,
					username));
		}

		String zipFile = zipPath + questionNumber + ".zip";
		String unzipDest = zipPath + questionNumber;

		boolean unzipped = fileService.unzip(zipFile, unzipDest);
		// It will unzip to directory question directory

		if (!unzipped) {
			log.error(String.format("%s Unzip Failed for %s: Homework (%s), question (%s)", log_prepend, zipFile,
					homework, username));
		}

		// delete non code-file [writeup] from unzip dir
		boolean deleteNonCodeFiles = fileService.deleteNonCodeFiles(unzipDest);

		if (!deleteNonCodeFiles) {
			log.error(String.format("%s deleteNonCodeFiles Failed %s: Homework (%s), question (%s)", log_prepend,
					zipFile, homework, username));
		}

		String jsonValidString = null;
		try {
			jsonValidString = compileAPIService.getJSONValidStringCode(unzipDest, problemName, language);
		} catch (Exception e) {
			log.error(log_prepend + " Error in getting json valid string: " + e.getMessage());
		}

		// Get all the test case files
		File[] testCaseFiles = fileService.getFiles(unzipTestCaseLoc);

		// Segregate input and output files
		ArrayList<File> inputTestCaseFiles = new ArrayList<>();
		ArrayList<File> outputTestCaseFiles = new ArrayList<>();

		for (File testCaseFile : testCaseFiles) {

			if (testCaseFile.getName().contains("input"))
				inputTestCaseFiles.add(testCaseFile);
			else if (testCaseFile.getName().contains("output"))
				outputTestCaseFiles.add(testCaseFile);
			else
				System.out.println("******* SHOULD NOT ENTER HERE *****, File:" + testCaseFile);
		}

		Collections.sort(inputTestCaseFiles);
		Collections.sort(outputTestCaseFiles);

		ArrayList<String> codeOutputList = new ArrayList<>();

		// All the files in test cases need to be run in API
		for (File testCaseFile : inputTestCaseFiles) {
			String testCaseString = compileAPIService.getJSONValidTestCase(testCaseFile);
			codeOutputList.add(compileAPIService.useJudge0API(jsonValidString, language, testCaseString));

		}

		// codeStatus: keeps track of code passed or failed the test case
		ArrayList<String> codeStatus = new ArrayList<>();
		ArrayList<String> expectedOutputList = new ArrayList<>();

		// Test case passed or not, evaluate the output with testcases
		for (int i = 0; i < outputTestCaseFiles.size(); i++) {

			boolean fileUploadedError = false;
			String expectedOutput = "";
			try {
				expectedOutput = fileService.getFileContent(outputTestCaseFiles.get(i)).trim();
			} catch (FileNotFoundException e) {
				log.error("Output File not found!");
				fileUploadedError = true;
			}

			String codeOutput = "";

			try {
				codeOutput = codeOutputList.get(i).trim();
			} catch (NullPointerException e) {
				codeOutput = "-";
			}

			if (fileUploadedError) {
				codeStatus.add("Output file not uploaded; Contact Professor!");
				expectedOutputList.add("error");

			} else if (expectedOutput.equals(codeOutput)) {
				codeStatus.add(PASS);
				expectedOutputList.add(expectedOutput);

			} else {
				codeStatus.add(FAIL);
				expectedOutputList.add(expectedOutput);
			}
		}

		// Save the codeOutput, expectedOutput and codeStatus to DB
		submissionDBService.saveOutputsAndResults(homework, username, questionNumber, codeOutputList,
				expectedOutputList, codeStatus);

		// delete unzip folder, testcases and code
		try {
			FileUtils.deleteDirectory(new File(unzipDest));
			log.debug(log_prepend + " deleted: " + unzipDest);
		} catch (IOException e) {
			log.error(log_prepend + " Unable to delete: " + unzipDest);
		}

		try {
			FileUtils.deleteDirectory(new File(unzipTestCaseLoc));
			log.debug(log_prepend + " deleted: " + unzipTestCaseLoc);
		} catch (IOException e) {
			log.error(log_prepend + " Unable to delete: " + unzipTestCaseLoc);
		}

		// Show confirmation to students of submitted file
		List<String> codeFileNames = submission.getFileNames(submission.codeFileType);
		List<String> writeupFileNames = submission.getFileNames(submission.writeupFileType);

		HashMap<String, Object> submissionConfirmData = new HashMap<>();

		submissionConfirmData.put("codeFileNames", codeFileNames);
		submissionConfirmData.put("writeupFileNames", writeupFileNames);
		submissionConfirmData.put("outputList", codeOutputList);
		submissionConfirmData.put("codeStatus", codeStatus);
		submissionConfirmData.put("expectedOutput", expectedOutputList);

		model.addAttribute("submissionConfirmationMap", submissionConfirmData);
		log.debug(log_prepend + "Displaying: student-confirmation");

		return "redirect:showConfirmation";
	}

	@GetMapping("/showConfirmation")
	public String showConfirmation(
			@SessionAttribute("submissionConfirmationMap") Map<String, Object> submissionConfirmData, Model model) {
		log.debug("[GET /showConfirmation] Displaying: student-confirmation");
		model.addAllAttributes(submissionConfirmData);
		return "student-confirmation";
	}

	/**
	 * This method redirects to showForm2. Also reinitializes question, codeFiles,
	 * and writeupFiles
	 *
	 * @param submission Session Attribute
	 * @return redirected to showForm2
	 */
	@PostMapping("/showConfirmation")
	public String showFormAgain(@SessionAttribute("submission") Submission submission) {
		String log_prepend = "[POST /showConfirmation]";
		submission.setProblemName(null);
		submission.setCodeFiles(null);
		submission.setWriteupFiles(null);
		log.debug(log_prepend + "Submission:" + submission);
		return "redirect:showForm2";
	}

	/**
	 * This method reinitializes all the fields in submission session attribute
	 * 
	 * It should redirect to validate page
	 * 
	 * @param submission Session Attribute
	 * @return redirected to showForm
	 */
	@PostMapping("/redirectHome")
	public String redirectHome(@SessionAttribute("submission") Submission submission) {
		String log_prepend = "[POST /redirectHome]";
		submission.setProblemName(null);
		submission.setCodeFiles(null);
		submission.setWriteupFiles(null);
		submission.setUsername(null);
		submission.setHomework(null);
		log.debug(log_prepend + "Submission:" + submission);
		return "redirect:showForm";
	}

}
