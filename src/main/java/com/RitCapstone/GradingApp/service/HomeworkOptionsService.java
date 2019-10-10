package com.RitCapstone.GradingApp.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RitCapstone.GradingApp.dao.HomeworkOptionsDAO;
import com.RitCapstone.GradingApp.dao.HomeworkOptionsDAOImpl;

@Service
public class HomeworkOptionsService {

	private static final String fileRestrictionJSON = "fileRestrictions.json";

	@Autowired
	HomeworkOptionsDAO homeworkOptionsDAO;

	private static Logger log = Logger.getLogger(HomeworkOptionsService.class);

	public Map<String, String> getHomeworkOptions() {
		log.debug(" Getting Homework Options!");
		return homeworkOptionsDAO.getHomeworkOptions();
	}

	public Map<String, String> getQuestionNameOptions(String hwId) {
		log.debug(" Getting Questions Options for: " + hwId);
		return homeworkOptionsDAO.getQuestionNameOptions(hwId);
	}

	public Map<String, Object> getFileRestrictionInfo() {

		Map<String, Object> map = new HashMap<>();

		ClassLoader classLoader = HomeworkOptionsService.class.getClassLoader();
		File jsonFile = new File(classLoader.getResource(fileRestrictionJSON).getFile());

		JSONParser parser = new JSONParser();

		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) parser.parse(new FileReader(jsonFile));

			map.put("codeExtension", (JSONArray) jsonObject.get("codeExtension"));
			map.put("writeupExtension", (JSONArray) jsonObject.get("writeupExtension"));
			map.put("langaugeOptions", (JSONArray) jsonObject.get("langaugeOptions"));

		} catch (IOException | ParseException e) {

			e.printStackTrace();
		}
		return map;

	}

	
	public static void main(String[] args) {

		HomeworkOptionsService service = new HomeworkOptionsService();
		service.homeworkOptionsDAO = new HomeworkOptionsDAOImpl();

//		System.out.println("*"+service.getQuestionNameOptions("3"));
//		service.getFileRestrictionInfo();
	}
}
