package com.RitCapstone.GradingApp.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.RitCapstone.GradingApp.mongo.MongoFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@Repository
public class GradingListDAOImpl implements GradingListDAO {

	private static Logger log = Logger.getLogger(GradingListDAOImpl.class);

	@Override
	public List<String> getListOfQuestions(String homework) {
		log.debug("Collection: " + homework + "; Distinct field: question");
		return getDistinct(homework, "question");
	}

	@Override
	public List<String> getListOfStudents(String homework) {
		log.debug("Collection: " + homework + "; Distinct field: username");
		return getDistinct(homework, "username");
	}

	@Override
	public List<String> getListOfStudentsForQuestion(String homework, String question) {

		String targetField = "username";
		String filterField = "question";

		return getDistinctForSpecificField(homework, question, targetField, filterField);
	}

	@Override
	public List<String> getListOfQuestionsForStudent(String homework, String username) {
		String targetField = "question";
		String filterField = "username";

		return getDistinctForSpecificField(homework, username, targetField, filterField);
	}

	private List<String> getDistinct(String collectionName, String field) {

		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, collectionName);

		List<String> returnList = new ArrayList<>();
		MongoCursor<String> it = collection.distinct(field, String.class).iterator();

		while (it.hasNext()) {
			String distinctField = it.next();
			returnList.add(distinctField);
		}
		log.debug("Number of distinct " + field + ": " + returnList.size());
		return returnList;

	}

	private List<String> getDistinctForSpecificField(String collectionName, String fieldName, String targetField,
			String filterField) {

		log.debug(String.format("Collection: %s, TargetField: %s, FilterField: %s [value: %s]", collectionName,
				targetField, filterField, fieldName));

		List<String> returnList = new ArrayList<>();

		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, collectionName);

		MongoCursor<String> it = collection.distinct(targetField, String.class)
				.filter(new Document(filterField, fieldName)).iterator();

		while (it.hasNext()) {
			String distinctField = it.next();
			returnList.add(distinctField);

		}
		log.debug(String.format("Number of distinct %s for %s %s: %d", targetField, filterField, fieldName,
				returnList.size()));

		return returnList;

	}

	public static void main(String[] args) {
//		System.out.println(new GradingListDAOImpl().getListOfStudents("Hw3"));
//		System.out.println(new GradingListDAOImpl().getListOfQuestionsForStudent("Hw3", "abc"));
	}

}
