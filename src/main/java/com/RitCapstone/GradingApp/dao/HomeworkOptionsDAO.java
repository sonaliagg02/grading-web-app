package com.RitCapstone.GradingApp.dao;

import java.util.Map;

// This DAO is responsible to get all homework names and questions related to them
public interface HomeworkOptionsDAO {
	
	public Map<String, String> getHomeworkOptions();
	
	public Map<String, String> getQuestionNameOptions(String hwId);
	
}
