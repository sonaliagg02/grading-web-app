package com.RitCapstone.GradingApp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

@Service
public class OnlineCompileAPIService {

	private static Logger log = Logger.getLogger(OnlineCompileAPIService.class);

	private static String API_URL = "https://api.judge0.com";
//	private static String API_URL = "http://localhost:3000";  // Refer to github page of Judge0

	@Autowired
	FileService fileService;

//	FileService fileService = new FileService(); // for testing only this file

	public String getJSONValidStringCode(String dirName, String mainFileName, String extension) throws Exception {

		File dir = new File(dirName);
		File[] listOfFiles = dir.listFiles();
		String jsonValidCodeString = "";
		if (extension.equals("")) {
			throw new Exception("unsupported format received: Allowed formats are '.java' and '.cpp'");
		} else if (extension.equals("Java")) {
			jsonValidCodeString = combineJava(listOfFiles, mainFileName);
		} else if (extension.equals("C++")) {
			jsonValidCodeString = combineCPP(listOfFiles);
		} else {
			throw new Exception("unsupported format received: " + extension);
		}

		return jsonValidCodeString;
	}

	private String combineCPP(File[] listOfFiles) throws IOException {

		// We have to separate header files and cpp files
		List<File> headerFiles = new ArrayList<>();
		List<File> cppFiles = new ArrayList<>();
		String output = "";

		for (File file : listOfFiles) {
			String extension = fileService.getExtension(file);

			if (extension.equals(".h")) {
				headerFiles.add(file);
			} else if (extension.equals(".cpp")) {
				cppFiles.add(file);
			} else {
				log.error("The file supplied is neither .cpp nor .h: " + file.getName());
			}
		}

		List<String> headerFileNames = new ArrayList<>();

		for (File headerFile : headerFiles) {
			headerFileNames.add(headerFile.getName());

			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(headerFile));
			String currentFileContent = "";

			while ((line = reader.readLine()) != null) {
				currentFileContent += line + "\n";
			}
			output += currentFileContent + "\n";
			reader.close();
		}

		for (File cppFile : cppFiles) {
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(cppFile));
			String currentFileContent = "";

			// Remove user-defined header:For each line we search of name of header file in
			// the line. If header file name is found we ignore that line
			while ((line = reader.readLine()) != null) {
				boolean headerPresent = false;
				for (String headerName : headerFileNames) {
					if (line.indexOf(headerName) == -1) {
						headerPresent = true;
					}
				}

				if (!headerPresent)
					currentFileContent += line + "\n";

			}

			output += currentFileContent + "\n";
			reader.close();
		}

		output = output.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\n", "\\n");

		return output;
	}

	private String combineJava(File[] listOfFiles, String mainFileName) throws IOException {

		String codeLines = "";
		String importLines = "";

		// We have to separate import statements from other rest of code
		for (File file : listOfFiles) {
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((line = reader.readLine()) != null) {
				if (line.indexOf("import") != -1) {
					importLines += line + "\n";
				} else if (line.indexOf("package") != -1) {
					// Ignore package lines
				} else {
					codeLines += line + "\n";
				}

			}

			reader.close();
		}

		String output = importLines + codeLines;
		output = output.replace("\\", "\\\\").replace("\"", "\\\"").replace("public class", "class")
				.replace(mainFileName, "Main").replace("\t", "\\t").replace("\n", "\\n");

		return output;
	}

	/**
	 * Method to replace line break and tabs with literal values "\n" , "\t" This
	 * ensures that the test case file contents are converted to JSON Valid string
	 * 
	 * @param testCaseFile File containing test case information
	 * @return JSONValidTestCaseString
	 */
	public String getJSONValidTestCase(File testCaseFile) {

		try {

//			Scanner sc = new Scanner(testCaseFile);
//			sc.useDelimiter("\\Z");
//			String fileOutput = sc.next();
//			fileOutput = fileOutput.replace("\t", "\\t").replace("\n", "\\n");
//			sc.close();
//			
			String fileOutput = "";

			BufferedReader br = new BufferedReader(new FileReader(testCaseFile));
			String line;

			while ((line = br.readLine()) != null) {
				line = line.replace("\t", "\\t");
				fileOutput += line + "\\n";
			}
			br.close();
			log.debug(testCaseFile.getName() + " converted to JSON Valid String");
			return fileOutput;

		} catch (IOException e) {
			log.error("Error in getJSONValidTestCase(): " + e.getMessage());
			return null;

		}

	}

	public String useJudge0API(String sourceCode, String language, String input) {

		HashMap<String, Integer> languageCode = new HashMap<>();

		// The language code used for Judge0 online compiler
		// For complete list of languages, using a REST client (For example: Postman)
		// GET https://api.judge0.com/languages
		languageCode.put("C++", 10);
		languageCode.put("Java", 27);
		languageCode.put("Python3", 34);

		try {

			int languageID = languageCode.get(language);

			String body = String.format("{\"source_code\": \"%s\", \"language_id\": %d, \"stdin\": \"%s\"}", sourceCode,
					languageID, input);

			HttpResponse<String> postResponse = Unirest.post(API_URL + "/submissions/?base64_encoded=false")
					.header("Content-Type", "application/json").header("cache-control", "no-cache").body(body)
					.asString();

			log.info("Status of Unirest POST operation: " + postResponse.getStatus());

			// convert string to JSON
			JSONParser parser = new JSONParser();
			JSONObject postResponseJson = (JSONObject) parser.parse(postResponse.getBody());
			String token = (String) postResponseJson.get("token");
			log.debug("Token: " + token);
			JSONObject getResponseJson = null;
			long statusId = -10;
			String messageFromAPI = "";
			while (true) {
				Thread.sleep(500);
				String getURL = String.format(API_URL + "/submissions/%s?base64_encoded=false", token);
				HttpResponse<String> getResponse = Unirest.get(getURL).header("Content-Type", "application/json")
						.header("cache-control", "no-cache").asString();

				log.info("Status of Unirest GET operation: " + getResponse.getStatus());

				String GETResponseBody = getResponse.getBody();

				/*
				 * The structure of getResponse is a dictionary inside a dictionary
				 * response[status][id] tells if the API is still running, finished processing
				 * or error occurred.
				 * 
				 * If id = 1, job is "In Queue"; If id = 2, job is "Processing"; If id = 3, the
				 * output is ready;
				 * 
				 * For complete list of status, using a REST client (For example: Postman)
				 * 
				 * GET https://api.judge0.com/statuses
				 */
				getResponseJson = (JSONObject) parser.parse(GETResponseBody);
				JSONObject getResponseJson_status = (JSONObject) getResponseJson.get("status");
				statusId = (Long) getResponseJson_status.get("id");
				messageFromAPI = (String) getResponseJson_status.get("description");

				if ((statusId == 1) || (statusId == 2)) {
					// job is not completed, check again in sometime
				} else {
					break;
				}
			}
			log.debug("Response: " + getResponseJson);
			String output = (String) getResponseJson.get("stdout");

			if (statusId >= 5 && statusId <= 13) {
				return "Compiler Says: " + messageFromAPI;
			}

			return output.trim();

		} catch (Exception e) {
			log.error("Error in useJudge0API(): " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String args[]) throws Exception {

		System.out.println("hi: If you receive NullPointerException, "
				+ "please comment @Autowired fileService, uncomment fileService");

		OnlineCompileAPIService api = new OnlineCompileAPIService();
		String lang = "C++";

		String code = api.getJSONValidStringCode(
				"/home/darryl/eclipse-workspace/grading-app/src/main/java/com/RitCapstone/GradingApp/service/temp",
				"Prerequisites", lang);

		System.out.println("@@@" + code + "@@@");

		String _input = api.getJSONValidTestCase(new File("/home/darryl/2018/Prerequisites/input-1.1"));
		System.out.println("~~~" + _input + "~~~");

		String output = api.useJudge0API(code, lang, _input);
		System.out.println(output);

		System.out.println("bye");
	}

}
