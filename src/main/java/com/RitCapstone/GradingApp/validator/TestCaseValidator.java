package com.RitCapstone.GradingApp.validator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.RitCapstone.GradingApp.Question;

@Component
public class TestCaseValidator implements Validator {

	private final static String fileRestrictionsJson = "fileRestrictions.json";

	@Override
	public boolean supports(Class<?> clazz) {
		// test case files are in Question class
		return Question.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Question question = (Question) target;
		CommonsMultipartFile[] testCases = null, outputTestCases = null;
		testCases = question.getTestCases();
		outputTestCases = question.getOutputTestCases();

		if (testCases.length != outputTestCases.length) {

			errors.rejectValue("testCases", "incorrectNumberOfFiles",
					"Number of inputs and outputs for test cases is not equal! Number of inputs:" + testCases.length
							+ " Number of outputs:" + outputTestCases.length);

			errors.rejectValue("outputTestCases", "incorrectNumberOfFiles",
					"Number of inputs and outputs for test cases is not equal! Number of inputs:" + testCases.length
							+ " Number of outputs:" + outputTestCases.length);
		} else {

			ClassLoader classLoader = TestCaseValidator.class.getClassLoader();
			File file = new File(classLoader.getResource(fileRestrictionsJson).getFile());
			JSONParser parser = new JSONParser();

			try {
				JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));

				long maxSize = (Long) jsonObject.get("Total maximum Size");
				String maxSizeString = (String) jsonObject.get("Total maximum Size String");

				// check if not greater than 1.5 MB
				for (CommonsMultipartFile multipartFile : testCases) {
					String field = "testCases";
					long size = multipartFile.getSize();

					if (size == 0) {
						errors.rejectValue(field, "missingFile", "No files uploaded");
					} else if (size > maxSize) {
						errors.rejectValue(field, "largeFile",
								"File uploaded in too large [Allowed " + maxSizeString + "]");
					}
				}

				for (CommonsMultipartFile multipartFile : outputTestCases) {
					long size = multipartFile.getSize();
					String field = "outputTestCases";
					if (size == 0) {
						errors.rejectValue(field, "missingFile", "No files uploaded");
					} else if (size > maxSize) {
						errors.rejectValue(field, "largeFile",
								"File uploaded in too large [Allowed " + maxSizeString + "]");
					}
				}

			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}

		}
	}

}
