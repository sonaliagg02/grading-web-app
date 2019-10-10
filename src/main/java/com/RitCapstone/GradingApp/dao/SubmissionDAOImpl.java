package com.RitCapstone.GradingApp.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.RitCapstone.GradingApp.mongo.MongoFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@Repository
public class SubmissionDAOImpl implements SubmissionDAO {

	private static Logger log = Logger.getLogger(SubmissionDAOImpl.class);
//	private static final String filenameOption = "fileName";
	private static final String pathOption = "path";
	private static final String pathAndFileOption = "pathAndFile";
	// Collection name is the hw_name

	@Override
	public String getSubmissionPath(String homework, String username, String question) {
		return getSubmissionFileOrPath(homework, username, question, pathOption);
	}

	@Override
	public String getSubmissionLocation(String homework, String username, String question) {
		return getSubmissionFileOrPath(homework, username, question, pathAndFileOption);
	}

	private String getSubmissionFileOrPath(String homework, String username, String question, String option) {

		log.info(String.format("Retrieving [%s]: Homework (%s), username (%s), question (%s)", option, homework,
				username, question));

		String collectionName = homework;
		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, collectionName);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("username", username);
		searchQuery.put("question", question);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (cursor.hasNext()) {
			Document doc = cursor.next();
			String path = doc.get("path", String.class);
			String filename = doc.get("fileName", String.class);

			switch (option) {

			case pathAndFileOption:
				return path + filename;

//			case filenameOption:
//				return filename;

			case pathOption:
				return path;

			default:
				log.warn("ENTERED THE DEFAULT BLOCK OF SWITCH CASE: option=" + option);
				return null;
			}

		} else {
			log.warn(String.format("No submission found: Homework (%s), username (%s), question (%s)", homework,
					username, question));
			return null;
		}

	}

	/**
	 * Method to create a submission
	 * 
	 * If a submission is already created, update the submission. Otherwise, 2
	 * records of same submission exists in the database
	 */
	@Override
	public boolean createSubmission(String homework, String username, String question, String zipPath,
			String zipFileName) {
		log.info(String.format("Creating new Submission, Homework (%s), username (%s), question (%s)", homework,
				username, question));

		String collectionName = homework;
		String databaseName = MongoFactory.getDatabaseName();

		try {
			MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, collectionName);

			Document doc = new Document();
			doc.put("username", username);
			doc.put("question", question);
			doc.put("path", zipPath);
			doc.put("fileName", zipFileName);

			// save submission to MongoDB
			collection.insertOne(doc);
			return true;

		} catch (Exception e) {
			log.error(e.getMessage());
			log.info(
					String.format("Error while creating new submission for Homework (%s), username (%s), question (%s)",
							homework, username, question));
			return false;
		}

	}

	/**
	 * Method to update a submission
	 * 
	 * If the submission is not created and the method is called, there is no effect
	 * because searchQuery return empty
	 */
	@Override
	public boolean updateSubmission(String homework, String username, String question, String zipPath,
			String zipFileName) {
		log.info(String.format("Updating Submission, Homework (%s), username (%s), question (%s)", homework, username,
				question));

		String collectionName = homework;
		String databaseName = MongoFactory.getDatabaseName();

		try {

			MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, collectionName);

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("username", username);
			searchQuery.put("question", question);

			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("fileName", zipFileName);
			newDocument.put("path", zipPath);

			BasicDBObject updateObject = new BasicDBObject();
			updateObject.put("$set", newDocument);

			// update submission to MongoDB
			collection.updateOne(searchQuery, updateObject);

			return true;

		} catch (Exception e) {
			log.error(e.getMessage());
			log.error(String.format("Error while updating submission for Homework (%s), username (%s), question (%s)",
					homework, username, question));
			return false;
		}
	}

	@Override
	public boolean addOutputListsAndResults(String homework, String username, String question, List<String> codeOutput,
			List<String> expectedOutput, List<String> codeStatus) {

		log.debug(String.format(
				"adding output and test case results" + "for Submission: Homework (%s), username (%s), question (%s)",
				homework, username, question));

		boolean isUpdate = false;
		boolean success = addOrUpdateOutputsAndResults(homework, username, question, codeOutput, expectedOutput,
				codeStatus, isUpdate);

		if (!success) {
			log.warn(String.format(
					"submission with no codeStatus and "
							+ " no codeOutput not found: Homework (%s), username (%s), question (%s)",
					homework, username, question));
		} else {
			log.debug(String.format("added codeOutput and codeStatus to: question [%s], username [%s], homework[%s]",
					question, username, homework));
		}
		return success;

	}

	@Override
	public boolean updateOutputListsAndResults(String homework, String username, String question,
			List<String> codeOutput, List<String> expectedOutput, List<String> codeStatus) {

		log.debug(String.format("updating output and test case results "
				+ "for Submission: Homework (%s), username (%s), question (%s)", homework, username, question));

		boolean isUpdate = true;
		boolean success = addOrUpdateOutputsAndResults(homework, username, question, codeOutput, expectedOutput,
				codeStatus, isUpdate);

		if (!success) {
			log.warn(String.format(
					"submission with already present codeStatus and "
							+ " already present codeOutput not found: Homework (%s), username (%s), question (%s)",
					homework, username, question));
		} else {
			log.debug(String.format("updated codeOutput and codeStatus to: question [%s], username [%s], homework[%s]",
					question, username, homework));
		}
		return success;
	}

	@Override
	public List<String> getCodeOutput(String homework, String username, String question) {
		String typeToReturn = "codeOutput";
		return getRequiredListFromSubmission(homework, username, question, typeToReturn);
	}

	@Override
	public List<String> getTestcaseResult(String homework, String username, String question) {
		String typeToReturn = "codeStatus";
		return getRequiredListFromSubmission(homework, username, question, typeToReturn);
	}

	@Override
	public List<String> getExpectedOutput(String homework, String username, String question) {
		String typeToReturn = "expectedOutput";
		return getRequiredListFromSubmission(homework, username, question, typeToReturn);
	}

	private List<String> getRequiredListFromSubmission(String homework, String username, String question,
			String typeToReturn) {

		log.debug("Retrieving " + typeToReturn);
		String collectionName = homework;
		String databaseName = MongoFactory.getDatabaseName();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, collectionName);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("username", username);
		searchQuery.put("question", question);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (cursor.hasNext()) {
			Document doc = cursor.next();
			List<String> toReturn = doc.get(typeToReturn, List.class);
			// Will cause ClassCastException if list does not contain only String

			log.debug("Retrieved [" + typeToReturn + "]:" + toReturn);
			return toReturn;

		} else {
			log.warn(String.format("No submission found: Homework (%s), username (%s), question (%s)", homework,
					username, question));
			return null;
		}

	}

	private boolean addOrUpdateOutputsAndResults(String homework, String username, String question,
			List<String> codeOutput, List<String> expectedOutput, List<String> codeStatus, boolean isUpdate) {

		String collectionName = homework;
		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, collectionName);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("username", username);
		searchQuery.put("question", question);
		searchQuery.put("codeOutput", new BasicDBObject("$exists", isUpdate));
		searchQuery.put("codeStatus", new BasicDBObject("$exists", isUpdate));
		searchQuery.put("expectedOutput", new BasicDBObject("$exists", isUpdate));

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (!cursor.hasNext()) {
			return false;
		} else {

			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("codeOutput", codeOutput);
			newDocument.put("expectedOutput", expectedOutput);
			newDocument.put("codeStatus", codeStatus);

			BasicDBObject updateObject = new BasicDBObject();
			updateObject.put("$set", newDocument);

			collection.updateOne(searchQuery, updateObject);
			return true;
		}
	}

	public static void main(String[] args) {

//		new SubmissionDAOImpl().addOutputAndTestCaseResults("Hw1", "TEST@rit.edu", "2", null, null);

//		new SubmissionDAOImpl().updateOutputAndTestCaseResults("Hw1", "TEST@rit.edu", "2", new ArrayList<>(),
//				new ArrayList<>());

//		new SubmissionDAOImpl().getTestcaseResult("Hw1", "tezt10@rit.edu", "1");
	}

}
