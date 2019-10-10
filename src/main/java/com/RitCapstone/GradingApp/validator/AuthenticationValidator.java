package com.RitCapstone.GradingApp.validator;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.RitCapstone.GradingApp.ProfessorAndGrader;

@Component
public class AuthenticationValidator implements Validator {

	private final static String professorAuthFile = "Authentication/professor.json";
	private final static String graderAuthFile = "Authentication/grader.json";

	public static final String professorString = "Professor";
	public static final String graderString = "Grader";

	private String userType;

	public AuthenticationValidator() {
		userType = "";
	}

	public void setUser(String user) {
		this.userType = user;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return ProfessorAndGrader.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		ProfessorAndGrader user = (ProfessorAndGrader) target;
		if (user.getUsername() == null || user.getPassword() == null)
			return;

		String fileToUse = "";

		if (this.userType.equals(professorString))
			fileToUse = professorAuthFile;
		else if (this.userType.equals(graderString))
			fileToUse = graderAuthFile;
		else
			throw new UnsupportedOperationException("user type not asisgned: Professor or Grader");

		ClassLoader classLoader = AuthenticationValidator.class.getClassLoader();
		File file = new File(classLoader.getResource(fileToUse).getFile());

		try {
			JSONParser parser = new JSONParser();
			JSONArray userList = (JSONArray) parser.parse(new FileReader(file));

			boolean validUser = false;
			for (Object object : userList) {
				JSONObject credentials = (JSONObject) object;

				String username = (String) credentials.get("username");
				String password = (String) credentials.get("password");

				if (user.getUsername().equals(username) && user.getPassword().equals(password))
					validUser = true;
			}

			if (!validUser)
				errors.rejectValue("incorrectCredentials", "incorrect.Credentials",
						"Entered username or password is incorrect.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
