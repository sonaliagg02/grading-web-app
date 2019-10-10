package com.RitCapstone.GradingApp.validator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.RitCapstone.GradingApp.Submission;

@Component
public class SubmissionValidator implements Validator {

	private final static String fileRestrictionsJson = "fileRestrictions.json";
	
	private void validationHelper(String field, CommonsMultipartFile[] files, HashSet<String> extensionSet, long maxSize,
			String maxSizeString, Errors errors) {
		long sum = 0;
		for (CommonsMultipartFile file : files) {

			long size = file.getSize();
			String filename = file.getOriginalFilename();
			String[] _parts = filename.trim().split("\\.");
			String extension = "." + _parts[_parts.length - 1];

			if (size == 0) {
				errors.rejectValue(field, "missingFile", "No files uploaded");
			} else if (!extensionSet.contains(extension)) {
				errors.rejectValue(field, "incorrectExtension", "Allowed files: " + extensionSet);
			} else {
				sum += size;
			}

		}
		if (sum > maxSize) {
			errors.rejectValue(field, "largeFile", "Total size of uploads is greater than " + maxSizeString);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Submission.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {


		Submission submission = (Submission) target;

		ClassLoader classLoader = SubmissionValidator.class.getClassLoader();
		File file = new File(classLoader.getResource(fileRestrictionsJson).getFile());

		// File Restrictions are read from JSON
		JSONParser parser = new JSONParser();

		try {
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
			long maxSize = (Long) jsonObject.get("Total maximum Size");
			String maxSizeString = (String) jsonObject.get("Total maximum Size String");

			JSONArray _codeExt = (JSONArray) jsonObject.get("codeExtension");
			JSONArray _writeupExt = (JSONArray) jsonObject.get("writeupExtension");

			HashSet<String> codeExt = new HashSet<>();
			HashSet<String> writeupExt = new HashSet<>();

			Iterator<?> it = _writeupExt.iterator();
			while (it.hasNext())
				writeupExt.add(it.next().toString());

			it = _codeExt.iterator();
			while (it.hasNext())
				codeExt.add(it.next().toString());

			validationHelper("codeFiles", submission.getCodeFiles(), codeExt, maxSize, maxSizeString, errors);
			validationHelper("writeupFiles", submission.getWriteupFiles(), writeupExt, maxSize, maxSizeString, errors);

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

	}

}
