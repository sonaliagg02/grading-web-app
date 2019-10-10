package com.RitCapstone.GradingApp.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class MarksAndFeedbackDAOImpl implements MarksAndFeedbackDAO {

	private static final String gradeCollection = "grade";

	private static Logger log = Logger.getLogger(MarksAndFeedbackDAOImpl.class);

	@Override
	public boolean createMarksAndFeedback(String homework, String username, String question, Integer marks,
			String feedback) {
		log.info(String.format("Entering marks: Hw (%s), question (%s), username (%s)", homework, question, username));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", question);
		map.put("username", username);

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, gradeCollection);
		BasicDBObject searchQuery = new BasicDBObject(map);
		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (cursor.hasNext()) {
			log.warn(String.format("Marks already exist:  Hw (%s), question (%s), username (%s)", homework, question,
					username));
			return false;
		} else {

			try {
				map.put("marks", marks);
				map.put("feedback", feedback);
				Document doc = new Document(map);
				collection.insertOne(doc);
				return true;
			} catch (Exception e) {
				log.error("Exception occurred in createMarksAndFeedback:" + e.getMessage());
				return false;
			}
		}
	}

	@Override
	public boolean updateMarksAndFeedback(String homework, String username, String question, Integer marks,
			String feedback) {
		log.info(String.format("Updating Marks: Homework (%s), question (%s), student (%s)", homework, question,
				username));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", question);
		map.put("username", username);

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, gradeCollection);
		BasicDBObject searchQuery = new BasicDBObject(map);
		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (!cursor.hasNext()) {
			log.warn(String.format("Marks doesn't exist: Homework (%s), question (%s), student (%s)", homework,
					question, username));
			return false;
		} else {
			try {
				map.put("marks", marks);
				map.put("feedback", feedback);

				BasicDBObject newDocument = new BasicDBObject(map);
				BasicDBObject updateObject = new BasicDBObject();
				updateObject.put("$set", newDocument);
				collection.updateOne(searchQuery, updateObject);
				return true;
			} catch (Exception e) {
				log.error("Exception occurred in createMarksAndFeedback:" + e.getMessage());
				return false;
			}
		}
	}

	@Override
	public Integer getMarks(String homework, String username, String question) {
		// TODO Auto-generated method stub
		String fieldName = "marks";
		String marksStr = getField(homework, username, question, fieldName);
		if (marksStr != null)
			return Integer.parseInt(marksStr);
		else
			return null;
	}

	@Override
	public String getFeedback(String homework, String username, String question) {
		String fieldName = "feedback";
		String feedback = getField(homework, username, question, fieldName);
		return feedback;
	}

	private String getField(String homework, String username, String question, String fieldName) {

		log.info(String.format("Retrieving [%s]: Homework (%s), username (%s), question (%s)", fieldName, homework,
				username, question));

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, gradeCollection);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("homework", homework);
		searchQuery.put("username", username);
		searchQuery.put("question", question);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (cursor.hasNext()) {
			Document doc = cursor.next();

			switch (fieldName) {
			case "marks":
				Integer marks = doc.get("marks", Integer.class);
				log.debug("Marks: " + marks);
				return marks.toString();

			case "feedback":
				String feedback = doc.get("feedback", String.class);
				log.debug("Feedback: " + feedback);
				return feedback;

			default:
				log.warn("ENTERED THE DEFAULT BLOCK OF SWITCH CASE: fieldName=" + fieldName);
				return null;
			}

		} else {
			log.warn(String.format("No marks or feebcak found: Homework (%s), username (%s), question (%s)", homework,
					username, question));
			return null;
		}

	}

	@Override
	public List<String> getDistinctStudents(String homework) {

		List<String> students = new ArrayList<>();
		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, gradeCollection);

		MongoCursor<String> it = collection.distinct("username", String.class)
				.filter(new Document("homework", homework)).iterator();

		while (it.hasNext()) {
			String student = it.next();
			students.add(student);
		}

		log.debug("Student List for Hw (" + homework + "):" + students);
		return students;

	}

	@Override
	public List<String> getQuestionsForStudent(String homework, String username) {

		List<String> questions = new ArrayList<>();
		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, gradeCollection);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("homework", homework);
		searchQuery.put("username", username);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		while (cursor.hasNext()) {
			Document doc = cursor.next();
			questions.add(doc.get("question", String.class));
		}
		log.debug("Completed grading questions for homework (" + homework + "), username(" + username + "): "
				+ questions);
		return questions;
	}

	public static void main(String[] args) {
		System.out.println("hi");

// 		boolean bool = new MarksAndFeedbackDAOImpl().createMarksAndFeedback("DEL", "abc", "2", 10, "Good jab");
//		System.out.println(bool);

//		int marks = new MarksAndFeedbackDAOImpl().getMarks("DEL", "abc", "2");
//		String feedback = new MarksAndFeedbackDAOImpl().getFeedback("DEL", "abc", "2");
//
//		System.out.println(marks + "-~-" + feedback);

		System.out.println("bye");
	}

}
