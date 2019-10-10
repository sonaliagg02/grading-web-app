package com.RitCapstone.GradingApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class Submission {

	public final String codeFileType = "CODE";
	public final String writeupFileType = "WRITEUP";

	@NotBlank(message = "RIT username is required")
	private String username;

	@NotNull(message = "homework is required")
	private String homework;

	@NotNull(message = "problem name is required")
	private String problemName;

	@NotNull(message = "Select language")
	private String language;

	private CommonsMultipartFile[] codeFiles;

	private CommonsMultipartFile[] writeupFiles;
	
	private Date date;

	public Submission() {
		this.username = null;
		this.homework = null;
		this.problemName = null;
		this.language = null;
	}

	@Override
	public String toString() {
		try {
			return "Submission [username=" + username + ", homework=" + homework + ", problemName=" + problemName
					+ ", langauge=" + language + ", codeFiles=" + Arrays.toString(codeFiles) + ", writeupFiles="
					+ Arrays.toString(writeupFiles) + "]";
		} catch (NullPointerException e) {
			return "Submission [username=" + username + ", homework=" + homework + ", problemName=" + problemName
					+ ", langauge=" + language + "]";
		}
	}

	public List<String> getFileNames(String type) {

		List<String> fileNames = new ArrayList<String>();
		CommonsMultipartFile[] multipartFiles;

		if (type.equals(codeFileType))
			multipartFiles = codeFiles;

		else if (type.equals(writeupFileType))
			multipartFiles = writeupFiles;
		else
			multipartFiles = null;

		for (CommonsMultipartFile multipartFile : multipartFiles) {
			fileNames.add(multipartFile.getOriginalFilename());
		}
		return fileNames;

	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHomework() {
		return homework;
	}

	public void setHomework(String homework) {
		this.homework = homework;
	}

	public String getProblemName() {
		return problemName;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public CommonsMultipartFile[] getCodeFiles() {
		return codeFiles;
	}

	public void setCodeFiles(CommonsMultipartFile[] codeFiles) {
		this.codeFiles = codeFiles;
	}

	public CommonsMultipartFile[] getWriteupFiles() {
		return writeupFiles;
	}

	public void setWriteupFiles(CommonsMultipartFile[] writeupFiles) {
		this.writeupFiles = writeupFiles;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
