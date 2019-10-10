package com.RitCapstone.GradingApp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class FileService {

	private static Logger log = Logger.getLogger(FileService.class);

	private static final String fileRestrictionJSON = "fileRestrictions.json";
	private static final String mappingResourcesJSON = "mappingResources.json";

	private static final String DIRECTORY_TO_SAVE = "uploads_from_springMVC";
	private static final String chosenDir = System.getProperty("user.dir") + File.separator + DIRECTORY_TO_SAVE
			+ File.separator;

	/**
	 * Method to zip the submission
	 * 
	 * @param filesToZipPath The path to files that are to be zipped
	 * @param zipName        Name of the zip file
	 * @param zipFileDest    Destination of the zip file, After creating the zip
	 *                       file, we move it
	 * @throws IOException
	 */
	private void zip(String filesToZipPath, String zipName, String zipFileDest) throws IOException {

		File dir = new File(filesToZipPath);
		File[] listOfFiles = dir.listFiles();

		File zipFile = new File(filesToZipPath + zipName);

		// Stream to save to zip file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		// saving all files to zip
		for (File file : listOfFiles) {
			if (file.isFile()) {
				log.debug(String.format("Zipping into %s: %s", zipName, file.getName()));

				ZipEntry zipEntry = new ZipEntry(file.getName());
				out.putNextEntry(zipEntry);

				byte[] data = IOUtils.toByteArray(new FileInputStream(file));
				out.write(data, 0, data.length);
				out.closeEntry();

			}
		}
		out.close();

		log.info(String.format("Moving %s to %s", zipFile.getAbsolutePath(), zipFileDest));

		// Move the zip folder out of current folder to zipFileDest
		try {
			FileUtils.moveFileToDirectory(zipFile, new File(zipFileDest), false);
			// Throws an exception if zipFile already exists,
			// In that case we delete the zip-file [It is the zip file from previous
			// submission]

		} catch (FileExistsException e) {
			log.debug(e.getMessage());
			FileUtils.deleteQuietly(new File(zipFileDest + zipName));
			log.debug("Deleted stale zip file");
			FileUtils.moveFileToDirectory(zipFile, new File(zipFileDest), false);
			// false will avoid creating a dir if dir does not exist

		}

		// Delete the question folder as it has been zipped
		FileUtils.deleteDirectory(new File(filesToZipPath));
		log.info("Deleted " + filesToZipPath);

	}

	/**
	 * Method to process the files, get file names and save the files on local
	 * machine
	 * 
	 * The file hierarchy is similar to try system. [It is present in:
	 * grading-app/src/main/resources/]
	 * 
	 * @param username     RIT username
	 * @param homework     Homework number
	 * @param question     Question number
	 * @param codeFiles    code files that are uploaded
	 * @param writeupFiles writeup files that are uploaded
	 * @return path of the zipped file
	 */
	public String saveStudentSubmission(String homework, String username, String question,
			CommonsMultipartFile[] codeFiles, CommonsMultipartFile[] writeupFiles) {

		String currentPath = chosenDir + homework + File.separator + username + File.separator + question
				+ File.separator;

		File destinationDir = new File(currentPath);

		// When the user uploads and the question directory already exists, delete the
		// directory
		if (destinationDir.exists()) {
			try {

				// Should not have entered here as we delete the folder after zipping
				log.warn(destinationDir.getAbsolutePath() + " exists!! Deleting it [Should not have entered here]");
				FileUtils.deleteDirectory(destinationDir);

			} catch (IOException e) {
				log.error("Error deleting " + destinationDir.getName() + " : " + e.getMessage());
			}

		}

		// To create the directory if it is not there
		log.debug("Creating " + destinationDir.getAbsolutePath());
		new File(currentPath + ".tmp").mkdirs();

		CommonsMultipartFile[] files = (CommonsMultipartFile[]) ArrayUtils.addAll(codeFiles, writeupFiles);
		for (CommonsMultipartFile file : files) {

			try {
				// Copy the uploaded file to local machine
				FileCopyUtils.copy(file.getBytes(), new File(currentPath + file.getOriginalFilename()));

			} catch (IOException e) {
				log.error("Error copying uploaded files to local: " + e.getMessage());
			} catch (IllegalStateException e) {
				log.error("Error copying uploaded files to local: " + e.getMessage());
				e.printStackTrace();
			}
		}

		String zipFileDest = chosenDir + homework + File.separator + username + File.separator;
		try {
			zip(currentPath, question + ".zip", zipFileDest);
		} catch (IOException e) {
			log.error("Error zipping the student submission: " + e.getMessage());
		}
		return zipFileDest;

	}

	public String saveTestCasesToLocal(CommonsMultipartFile[] testCases, CommonsMultipartFile[] outputTestCases) {
		int count;

		String testCasePath = chosenDir + ".testCases" + File.separator;
		new File(testCasePath).mkdirs();

		count = 1;
		for (CommonsMultipartFile file : testCases) {
			try {

				FileCopyUtils.copy(file.getBytes(), new File(testCasePath + "input_" + count++));

			} catch (IOException e) {
				log.error("Error saving INPUT test cases to local: " + e.getMessage());
			}
		}
		count = 1;
		for (CommonsMultipartFile file : outputTestCases) {
			try {

				FileCopyUtils.copy(file.getBytes(), new File(testCasePath + "output_" + count++));

			} catch (IOException e) {
				log.error("Error saving OUTPUT test cases to local: " + e.getMessage());
			}
		}
		return testCasePath;
	}

	/**
	 * Method to unzip a zip file an d save the files to destDir
	 * 
	 * @param zipFile zip file to unzip
	 * @param destDir the directory where files will be unzipped
	 * @return boolean indicating success/failure of unzip method
	 */
	public boolean unzip(String zipFile, String destDir) {

		File dir = new File(destDir);

		if (!dir.exists())
			dir.mkdirs();

		try {

			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null) {

				String fileName = zipEntry.getName();
				File newFile = new File(destDir + File.separator + fileName);

				log.debug("Unzipping to " + newFile.getAbsolutePath());
				FileOutputStream fileOutputStream = new FileOutputStream(newFile);

				int len;
				byte[] buffer = new byte[1024];

				while ((len = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, len);
				}

				fileOutputStream.close();
				zipInputStream.closeEntry();

				zipEntry = zipInputStream.getNextEntry();
			}

			zipInputStream.closeEntry();
			zipInputStream.close();
			return true;

		} catch (FileNotFoundException e) {
			log.error(zipFile + " does not exist!");
			return false;
		}

		catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}

	}

	/**
	 * Method to delete non Code files from the directory
	 * 
	 * @param dirName Directory from where the non-code files will be deleted
	 * @return boolean indicating success/failure of deleteNonCodeFiles method
	 */
	public boolean deleteNonCodeFiles(String dirName) {

		File[] listOfFiles = getFiles(dirName);
		if (listOfFiles == null) {
			return false; // as listOfFiles is not a directory
		}
		// get the code extensions from fileRestrictions.json
		ClassLoader classLoader = FileService.class.getClassLoader();
		File jsonFile = new File(classLoader.getResource(fileRestrictionJSON).getFile());

		JSONParser parser = new JSONParser();

		HashSet<String> codeExtensionSet = new HashSet<>();
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(jsonFile));
			JSONArray _codeExt = (JSONArray) jsonObject.get("codeExtension");

			Iterator<?> it = _codeExt.iterator();
			while (it.hasNext())
				codeExtensionSet.add(it.next().toString());

		} catch (Exception e) {
			log.error("Error while reading jsonFile in deleteFile: " + e.getMessage());
			return false;
		}

		try {
			for (File file : listOfFiles) {

				String filename = file.getName();
				String extension = getExtension(file);

				if (!codeExtensionSet.contains(extension)) {
					file.delete();
					log.info("File deleted: " + filename);
				}
			}
			return true;

		} catch (Exception e) {
			log.error("Error in deleteFile: " + e.getMessage());
			return false;
		}

	}

	public File[] getFiles(String dirName) {
		File dir = new File(dirName);
		if (!dir.isDirectory()) {
			log.error(dir.getName() + " is not a directory");
			return null;
		}

		return dir.listFiles();
	}

	public ArrayList<String> getFilenames(String dirName) {
		File[] files = getFiles(dirName);
		ArrayList<String> filenames = new ArrayList<>();

		for (File file : files) {
			filenames.add(file.getName());
		}

		return filenames;
	}

	public ArrayList<String> getMultipartFileNames(CommonsMultipartFile[] files) {

		ArrayList<String> names = new ArrayList<>();

		for (CommonsMultipartFile multipartFile : files) {
			names.add(multipartFile.getOriginalFilename());
		}

		return names;
	}

	public String getExtension(File file) {

		String[] _parts = file.getName().trim().split("\\.");
		String extension = "." + _parts[_parts.length - 1];
		return extension;

	}

	public String getFileContent(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		sc.useDelimiter("\\Z");
		String contents = sc.next();
		sc.close();
		return contents;
	}

	public String getURLLocation(String fileLocation) throws IOException, ParseException {
		ClassLoader classLoader = FileService.class.getClassLoader();
		File jsonFile = new File(classLoader.getResource(mappingResourcesJSON).getFile());

		JSONParser parser = new JSONParser();

		JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(jsonFile));
		String fileServerPath = (String) jsonObject.get("fileServerPath");
		String urlPath = (String) jsonObject.get("URLPath");

		return fileLocation.replace(fileServerPath, urlPath);

	}

	/**
	 * Reference: https://www.mkyong.com/java/itext-read-and-write-pdf-in-java/
	 * 
	 */
	public File writeToPDF(File fileToConvert, String pdfName) throws IOException, DocumentException {

		Document document = new Document();

		File outputFile = new File(pdfName);
		log.debug("Absolute Path of file to be converted: " + outputFile.getAbsolutePath());

		PdfWriter.getInstance(document, new FileOutputStream(outputFile));
		BufferedReader br = new BufferedReader(new FileReader(fileToConvert));
		try {
			document.open();
			String line;
			while ((line = br.readLine()) != null) {
				Paragraph p = new Paragraph();
				p.add(line);
				document.add(p);
			}
		} finally {

			document.close();
			br.close();
		}

		return outputFile;

	}

}
