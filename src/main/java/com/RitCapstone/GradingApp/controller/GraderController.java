package com.RitCapstone.GradingApp.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.RitCapstone.GradingApp.GradeHomework;
import com.RitCapstone.GradingApp.HomeworkOptions;
import com.RitCapstone.GradingApp.ProfessorAndGrader;
import com.RitCapstone.GradingApp.service.FileService;
import com.RitCapstone.GradingApp.service.GradingListService;
import com.RitCapstone.GradingApp.service.HomeworkOptionsService;
import com.RitCapstone.GradingApp.service.MarksAndFeedbackService;
import com.RitCapstone.GradingApp.service.SubmissionDBService;
import com.RitCapstone.GradingApp.validator.AuthenticationValidator;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Controller
@RequestMapping("/grader")
@SessionAttributes("gradeHomework")
public class GraderController {

	private static Logger log = Logger.getLogger(GraderController.class);

	private static final String gradeDir = "grade-";
	@Autowired
	AuthenticationValidator authValidator;

	@Autowired
	HomeworkOptionsService homeworkOptionsService;

	@Autowired
	GradingListService gradingListService;

	@Autowired
	SubmissionDBService submissionDBService;

	@Autowired
	FileService fileService;

	@Autowired
	MarksAndFeedbackService marksAndFeedbackService;

	/**
	 * This method will trim all the strings received from form data
	 */
	@InitBinder
	public void stringTrimmer(WebDataBinder dataBinder) {

		StringTrimmerEditor trimmer = new StringTrimmerEditor(true);
		// If after trimming the string is empty, it is converted to null {Set by true}

		dataBinder.registerCustomEditor(String.class, trimmer);
	}

	@GetMapping("/authenticate")
	public String showFormForAuthentication(Model model) {
		String jspToDisplay = "graderView/grader-authenticate";
		String log_prepend = "[GET /authenticate  (grader)]";

		if (!model.containsAttribute("grader")) {
			model.addAttribute("grader", new ProfessorAndGrader());
			log.debug(log_prepend + "adding grader to model");
		}

		log.debug(log_prepend + "model: " + model);
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		return jspToDisplay;
	}

	@PostMapping("/authenticate")
	public String authenticateGrader(@Valid @ModelAttribute("grader") ProfessorAndGrader grader,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		String log_prepend = "[POST /authenticate (grader)]";

		authValidator.setUser(AuthenticationValidator.graderString);
		authValidator.validate(grader, bindingResult);

		if (bindingResult.hasErrors()) {

			redirectAttributes.addFlashAttribute("grader", grader);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.grader", bindingResult);

			log.debug(log_prepend + "Redirecting to /authenticate");
			return "redirect:authenticate";
		}
		log.debug(log_prepend + " Login successful, Redirecting to /showForm");
		return "redirect:showForm";

	}

	@GetMapping("/showForm")
	public String showForm(Map<String, Object> model) {
		String jspToDisplay = "graderView/grade-homework-options";
		String log_prepend = "[GET /showForm]";

		if (!model.containsKey("gradeHomework")) {
			model.put("gradeHomework", new GradeHomework());
			log.debug(log_prepend + "adding gradeHomework object to model");

		}

		if (!model.containsKey("hw")) {
			HomeworkOptions hwOptions = new HomeworkOptions();
			hwOptions.setHomeworkOptions(homeworkOptionsService.getHomeworkOptions());
			model.put("hw", hwOptions);

			log.debug(log_prepend + "adding hw to model:" + hwOptions.getHomeworkOptions());

		}

		log.debug(log_prepend + "Displaying " + jspToDisplay);
		return jspToDisplay;
	}

	@PostMapping(value = "/showForm", params = { "listByStudent" })
	public String validateShowFormStudentList(@Valid @ModelAttribute("gradeHomework") GradeHomework gradeHwObject,
			BindingResult bindingResult, RedirectAttributes redirectAttribute) {

		String log_prepend = "[POST /showForm (listByStudent)]";
		boolean isCorrect = checkValidationForHwSelecetd(log_prepend, gradeHwObject, bindingResult, redirectAttribute);

		if (!isCorrect) {
			return "redirect:showForm";
		} else {
			// redirect to page where students are listed
			return "redirect:showStudentList";
		}

	}

	@PostMapping(value = "/showForm", params = { "listByQuestion" })
	public String validateShowFormQuestionList(@Valid @ModelAttribute("gradeHomework") GradeHomework gradeHwObject,
			BindingResult bindingResult, RedirectAttributes redirectAttribute) {

		String log_prepend = "[POST /showForm (listByQuestion)]";
		boolean isCorrect = checkValidationForHwSelecetd(log_prepend, gradeHwObject, bindingResult, redirectAttribute);

		if (!isCorrect) {
			return "redirect:showForm";
		} else {
			// redirect to page where questions are listed
			return "redirect:showQuestionList";
		}

	}

	private boolean checkValidationForHwSelecetd(String log_prepend, GradeHomework gradeHwObject,
			BindingResult bindingResult, RedirectAttributes redirectAttribute) {

		log.debug("In " + log_prepend);

		List<FieldError> homeworkSelectionError = bindingResult.getFieldErrors("homework");
		if (homeworkSelectionError.size() > 0) {
			redirectAttribute.addFlashAttribute("gradeHomework", gradeHwObject);
			redirectAttribute.addFlashAttribute("org.springframework.validation.BindingResult.gradeHomework",
					bindingResult);
			log.error("homework section for GradeHomework not selected:" + bindingResult);
			return false;
		}
		log.debug("homework section for GradeHomework selected:" + gradeHwObject.getHomework());
		return true;
	}

	@GetMapping("/showStudentList")
	public String showStudentList(Map<String, Object> model,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework) {

		String log_prepend = "[GET /showStudentList]";
		String jspToDisplay = "graderView/list-hw-by-student";
		log.debug(log_prepend + " Displaying " + jspToDisplay);
		model.put("studentList", gradingListService.getListOfStudents(gradeHomework.getHomework()));
		resetMarksAndFeedback(gradeHomework);

		return jspToDisplay;
	}

	@GetMapping("/showQuestionList")
	public String showQuestionList(Map<String, Object> model,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework) {

		String log_prepend = "[GET /showQuestionList]";
		String jspToDisplay = "graderView/list-hw-by-question";
		log.debug(log_prepend + " Displaying " + jspToDisplay);
		model.put("questionList", gradingListService.getListOfQuestions(gradeHomework.getHomework()));
		resetMarksAndFeedback(gradeHomework);

		return jspToDisplay;
	}

	@GetMapping("/student")
	public String showQuestionListForStudent(@RequestParam("studentName") String studentName, Map<String, Object> model,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework) {

		String log_prepend = "[GET /student (" + studentName + ")]";
		String jspToDisplay = "graderView/list-question-for-student";

		log.debug(log_prepend + " Displaying " + jspToDisplay);
		String homework = gradeHomework.getHomework();
		model.put("questionListForStudent", gradingListService.getListOfQuestionsForStudent(homework, studentName));
		model.put("studentName", studentName);
		resetMarksAndFeedback(gradeHomework);
		return jspToDisplay;

	}

	@GetMapping("/question")
	public String showStudentListForQuestion(@RequestParam("questionName") String questionName,
			Map<String, Object> model, @SessionAttribute("gradeHomework") GradeHomework gradeHomework) {

		String log_prepend = "[GET /question (" + questionName + ")]";
		String jspToDisplay = "graderView/list-student-for-question";

		log.debug(log_prepend + " Displaying " + jspToDisplay);
		String homework = gradeHomework.getHomework();
		model.put("studentListForQuestion", gradingListService.getListOfStudentsForQuestion(homework, questionName));
		model.put("questionName", questionName);
		resetMarksAndFeedback(gradeHomework);
		return jspToDisplay;
	}

	@GetMapping(value = "/questionStudent", params = { "studentName", "questionName" })
	public String showGradingView(@RequestParam("questionName") String questionName,
			@RequestParam("studentName") String studentName, Map<String, Object> model,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework) {

		String log_prepend = "[GET /questionStudent (" + questionName + ", " + studentName + ")]";
		String homework = gradeHomework.getHomework();
		String submissionLoc = submissionDBService.getSubmissionLocation(homework, studentName, questionName);
		log.debug(log_prepend + " Submission Location: " + submissionLoc);

		String submissionPath = submissionDBService.getSubmissionPath(homework, studentName, questionName);

		// To distinguish between unzipped files from multiple submissions
		String destDir = getDestinationPathFormat(submissionPath, questionName);

		boolean unzipped = fileService.unzip(submissionLoc, destDir);

		if (!unzipped) {
			log.error(log_prepend + " error while unzipping zipFile to " + destDir);
		}

		model.put("studentName", studentName);
		model.put("questionName", questionName);
		model.put("codeOutput", submissionDBService.getCodeOutput(homework, studentName, questionName));
		model.put("expectedOutput", submissionDBService.getExpectedOutput(homework, studentName, questionName));
		model.put("testcaseResult", submissionDBService.getTestcaseResult(homework, studentName, questionName));
		model.put("submittedFiles", fileService.getFilenames(destDir));

		return "graderView/enter-marks";
	}

	@PostMapping(value = "/questionStudent", params = { "studentName", "questionName" })
	public String submitMarks(@RequestParam("questionName") String questionName,
			@RequestParam("studentName") String studentName,
			@Valid @ModelAttribute("gradeHomework") GradeHomework gradeHwObject, BindingResult bindingResult,
			RedirectAttributes redirectAttribute) {

		if (bindingResult.hasErrors()) {
			redirectAttribute.addFlashAttribute("gradeHomework", gradeHwObject);
			redirectAttribute.addFlashAttribute("org.springframework.validation.BindingResult.gradeHomework",
					bindingResult);
			log.error("Marks not assisgned: " + bindingResult);

		} else {

			String homework = gradeHwObject.getHomework();
			int marks = gradeHwObject.getMarksGiven();
			String feedback = gradeHwObject.getFeedback();

			marksAndFeedbackService.saveMarksAndFeedback(homework, studentName, questionName, marks, feedback);

		}

		return "redirect:questionStudent?questionName=" + questionName + "&studentName=" + studentName;
	}

	private String getDestinationPathFormat(String submissionPath, String questionName) {
		return submissionPath + gradeDir + questionName + File.separator;
	}

	private void resetMarksAndFeedback(GradeHomework gradeHwObject) {
		gradeHwObject.setFeedback("");
		gradeHwObject.setMarksGiven(0);
	}

	@GetMapping(value = "/questionStudent", params = { "studentName", "questionName", "file" })
	public String showSubmissionFile(@RequestParam("questionName") String questionName,
			@RequestParam("studentName") String studentName, @RequestParam("file") String filename,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework, Map<String, Object> model) {

		String log_prepend = "[GET /questionStudent with file (" + filename + ")]";
		String homework = gradeHomework.getHomework();
		String submissionPath = submissionDBService.getSubmissionPath(homework, studentName, questionName);
		String fileLoc = getDestinationPathFormat(submissionPath, questionName) + filename;

		String urlLoc = "NOT-FOUND";

		try {
			urlLoc = fileService.getURLLocation(fileLoc);

		} catch (IOException | ParseException e) {
			log.error(log_prepend + " Error while getting urlPath for file " + filename + ": " + e.getMessage());
		}

		if (filename.endsWith(".pdf")) {
			log.debug(log_prepend + " Redirecting to:" + urlLoc);
			return "redirect:/" + urlLoc;
		}

		else {
			try {

				String extension = fileService.getExtension(new File(filename));
				log.debug(log_prepend + " file Extension: " + extension);
				String newFileName = filename.replace(extension, ".pdf");
				File outputFile = fileService.writeToPDF(new File(fileLoc), newFileName);

				try {
					urlLoc = fileService.getURLLocation(outputFile.getAbsolutePath());

				} catch (IOException | ParseException e) {
					log.error(
							log_prepend + " Error while getting urlPath for file " + filename + ": " + e.getMessage());
				}

				return "redirect:/" + urlLoc;

			} catch (IOException | DocumentException e) {
				log.error(log_prepend + " Can't convert " + filename + " to PDF: " + e.getMessage());
				return "redirect:/" + urlLoc;
			}
		}
	}

	@GetMapping(value = "/questionStudent", params = { "studentName", "questionName", "file", "original"})
	public String showSubmissionFileAsOriginal(@RequestParam("questionName") String questionName,
			@RequestParam("studentName") String studentName, @RequestParam("file") String filename,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework, Map<String, Object> model) {

		String log_prepend = "[GET /questionStudent with file (" + filename + ")]";
		String homework = gradeHomework.getHomework();
		String submissionPath = submissionDBService.getSubmissionPath(homework, studentName, questionName);
		String fileLoc = getDestinationPathFormat(submissionPath, questionName) + filename;

		String urlLoc = "NOT-FOUND";

		try {
			urlLoc = fileService.getURLLocation(fileLoc);

		} catch (IOException | ParseException e) {
			log.error(log_prepend + " Error while getting urlPath for file " + filename + ": " + e.getMessage());
		}

		return "redirect:/" + urlLoc;
	}
	
	@GetMapping("/showCompletedGrading")
	public String showCompletedGrading(Map<String, Object> model,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework) {

		String log_prepend = "[GET /showCompletedGrading]";
		Triple<List<String>, List<List<String>>, List<List<Integer>>> triple = marksAndFeedbackService
				.getCompletedGrading(gradeHomework.getHomework());

		log.debug(log_prepend + " Completed grading: " + triple);

		List<String> students = triple.getLeft();
		List<List<String>> questions = triple.getMiddle();
		List<List<Integer>> marks = triple.getRight();

		model.put("studentList", students);
		model.put("questionListForStudent", questions);
		model.put("marksListForStudent", marks);

		log.debug("Displaying get-graded-files");
		return "graderView/get-graded-files";
//		return "TEMP";
	}

	@GetMapping("/Feedback")
	public String getFeedback(@RequestParam("questionName") String questionName,
			@RequestParam("studentName") String studentName,
			@SessionAttribute("gradeHomework") GradeHomework gradeHomework, Map<String, Object> model) {

		model.put("marks", marksAndFeedbackService.getMarks(gradeHomework.getHomework(), studentName, questionName));
		model.put("feedback", marksAndFeedbackService.getFeedback(gradeHomework.getHomework(), studentName, questionName));

		log.debug("Displaying get-feedback");
		return "graderView/get-feedback";

	}

}
