package com.RitCapstone.GradingApp.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.RitCapstone.GradingApp.mongo.MongoFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@Repository
public class QuestionDAOImpl implements QuestionDAO {

	private static final String questionMetadataColl = "questionMetadata";

	private static Logger log = Logger.getLogger(QuestionDAOImpl.class);

	@Override
	public Map<String, Object> getQuestionMetaData(String homework, String questionNumber) {

		String databaseName = MongoFactory.getDatabaseName();
		Map<String, Object> metadata = new HashMap<>();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("homework", homework);
		searchQuery.put("question", questionNumber);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		while (cursor.hasNext()) {
			Document doc = cursor.next();

			metadata.put("question", doc.get("question", String.class));
			metadata.put("homework", doc.get("homework", String.class));
			metadata.put("dueDate", doc.get("dueDate", Date.class));
			metadata.put("problemName", doc.get("problemName", String.class));

		}
		return metadata;
	}

	@Override
	public boolean createQuestionMetaData(String homework, String questionNumber, String problemName,
			String description, Date dueDate) {
		log.info(
				String.format("Creating new questionMetaData, Homework (%s), question (%s)", homework, questionNumber));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", questionNumber);

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);
		BasicDBObject searchQuery = new BasicDBObject(map);
		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (cursor.hasNext()) {
			log.warn(String.format("questionMetaData already exists: Homework (%s), question (%s)", homework,
					questionNumber));
			return false;
		} else {
			try {
				map.put("problemName", problemName);
				map.put("description", description);
				map.put("dueDate", dueDate);
				Document doc = new Document(map);
				collection.insertOne(doc);
				return true;
			} catch (Exception e) {
				log.error("Exception occurred in createTestCase:" + e.getMessage());
				return false;
			}
		}

	}

	@Override
	public boolean updateQuestionMetaData(String homework, String questionNumber, String problemName,
			String description, Date dueDate) {
		log.info(String.format("Updating questionMetaData, Homework (%s), question (%s)", homework, questionNumber));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", questionNumber);

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);
		BasicDBObject searchQuery = new BasicDBObject(map);
		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (!cursor.hasNext()) {
			log.warn(String.format("questionMetaData does not exist: Homework (%s), question (%s)", homework,
					questionNumber));
			return false;
		} else {
			try {
				map.put("problemName", problemName);
				map.put("description", description);
				map.put("dueDate", dueDate);

				BasicDBObject newDocument = new BasicDBObject(map);
				BasicDBObject updateObject = new BasicDBObject();
				updateObject.put("$set", newDocument);
				collection.updateOne(searchQuery, updateObject);
				return true;
			} catch (Exception e) {
				log.error("Exception occurred in updateTestCase:" + e.getMessage());
				return false;
			}
		}
	}

	@Override
	public String getQuestionNumber(String homework, String problemName) {

		String databaseName = MongoFactory.getDatabaseName();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("problemName", problemName);
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);
		BasicDBObject searchQuery = new BasicDBObject(map);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (!cursor.hasNext()) {
			log.warn("No question Number for Problem: " + problemName);
			return "";
		} else {
			Document doc = cursor.next();
			String question = doc.get("question", String.class);
			log.debug("Question number for Problem " + problemName + " is: " + question);
			return question;
		}

	}

	@Override
	public Date getDueDate(String homework) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);

		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);
		BasicDBObject searchQuery = new BasicDBObject(map);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (!cursor.hasNext()) {
			log.warn("Date not retrieved! No homework found: " + homework);
			return null;
		} else {
			Document doc = cursor.next();
			Date date = doc.get("dueDate", Date.class);
			log.debug("DueDate for " + homework + " is: " + date);
			return date;
		}

	}

	public static void main(String[] args) {
		System.out.println("hi");
//		System.out.println(new QuestionDAOImpl().getQuestionMetaData("hwQM", "1"));
//		System.out.println(new QuestionDAOImpl().getQuestionNumber("3", "Dourado"));
		System.out.println("bye");
	}

}
