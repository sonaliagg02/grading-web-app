package com.RitCapstone.GradingApp.mongo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoFactory {

	private static Logger log = Logger.getLogger(MongoFactory.class);

	public final static String mongoClientJSON = "mongoClient.json";

	private static MongoClient mongo;

	private static String DATABASE_NAME = null;

	private MongoFactory() {
	}

	/**
	 * Method to read database name as mentioned in mongoClient_JSON To avoid
	 * reading from the file repeatedly, we read once, save it to DATABASE_NAME and
	 * return it when the method is called again
	 * 
	 * @return
	 */
	public static String getDatabaseName() {

		if (DATABASE_NAME == null) {
			ClassLoader classLoader = MongoFactory.class.getClassLoader();
			File file = new File(classLoader.getResource(mongoClientJSON).getFile());
			JSONParser parser = new JSONParser();

			try {
				JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
				DATABASE_NAME = (String) jsonObject.get("databaseName");
				return DATABASE_NAME;

			} catch (IOException | ParseException e) {

				log.error("Error in MongoFactory.getDatabaseName(): " + e.getMessage());
				return null;
			}

		} else {
			return DATABASE_NAME;
		}

	}

	/**
	 * Method to get MongoClient
	 * 
	 * @return MongoClient object at hostname and port given in mongoClient_JSON
	 */
	public static MongoClient getMongoClient() {

		if (mongo == null) {

			ClassLoader classLoader = MongoFactory.class.getClassLoader();
			File file = new File(classLoader.getResource(mongoClientJSON).getFile());

			JSONParser parser = new JSONParser();

			try {
				JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
				String hostname = (String) jsonObject.get("hostname");
				long port = (Long) jsonObject.get("port");
				
				mongo = new MongoClient(hostname, (int) port);
				log.debug(String.format("MongoClient: Hostname: %s \t Port:%d", hostname, port));

			} catch (IOException | ParseException | MongoException e) {
				log.error("Error in MongoFactory.getMongoClient(): " + e.getMessage());
			}
		}
		return mongo;

	}

	/**
	 * To retrieve the database. If the database is not present, MongoDB will create
	 * one
	 * 
	 * @param databaseName Name of dataBase
	 * @return MongoDatabase to be returned
	 */
	public static MongoDatabase getDatabase(String databaseName) {
		return getMongoClient().getDatabase(databaseName);
	}

	// MongoClient.getDB(dbName) is deprecated but is the only constructor for
	// GridFS
	@SuppressWarnings("deprecation")
	public static DB getDB(String databaseName) {
		return getMongoClient().getDB(databaseName);
	}

	/**
	 * To retrieve the collection from database. If the collection is not present,
	 * MongoDB will create one
	 * 
	 * @param databaseName   Name of dataBase
	 * @param collectionName Name of the collection
	 * @return MongoDatabase to be returned
	 */
	public static MongoCollection<Document> getCollection(String databaseName, String collectionName) {
		return getDatabase(databaseName).getCollection(collectionName);
	}

}
