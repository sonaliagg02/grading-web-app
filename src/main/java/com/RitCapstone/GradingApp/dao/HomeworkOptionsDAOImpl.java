package com.RitCapstone.GradingApp.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import com.RitCapstone.GradingApp.mongo.MongoFactory;

@Repository
public class HomeworkOptionsDAOImpl implements HomeworkOptionsDAO {

	private static final String questionMetadataColl = "questionMetadata";

	private static Logger log = Logger.getLogger(HomeworkOptionsDAOImpl.class);

	@Override
	public Map<String, String> getHomeworkOptions() {
		Map<String, String> homeworkOptions = new LinkedHashMap<>();
		homeworkOptions.put("", "Select Homework");

		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);

		MongoCursor<String> it = collection.distinct("homework", String.class).iterator();

		while (it.hasNext()) {
			String option = it.next();
			homeworkOptions.put(option, option);
		}

		log.debug("Homework Options:" + homeworkOptions);
		return homeworkOptions;
	}

	@Override
	public Map<String, String> getQuestionNameOptions(String hwId) {
		Map<String, String> questionOptions = new LinkedHashMap<>();
		questionOptions.put("", "Select Question");

		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);

		MongoCursor<String> it = collection.distinct("problemName", String.class).filter(new Document("homework", hwId))
				.iterator();

		while (it.hasNext()) {
			String option = it.next();
			questionOptions.put(option, option);

		}

		log.debug("Question Options:" + questionOptions);
		return questionOptions;

	}

	public static void main(String[] args) {
		new HomeworkOptionsDAOImpl().getQuestionNameOptions("3");
//		new HomeworkOptionsDAOImpl().getHomeworkOptions();
	}

}
