<%@page import="com.RitCapstone.GradingApp.GradeHomework"%>
<%@page import="org.springframework.ui.Model"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Grade Student</title>

<style>
.error {
	color: red
}

html {
	display: table;
	margin: auto;
}

body {
	display: table-cell;
	vertical-align: middle;
}

.formfield * {
	vertical-align: middle;
}
</style>
</head>
<body>

	<h3>Enter marks!</h3>

	Homework: ${gradeHomework.homework}

	<br> Student Name:
	<b>${studentName}</b>

	<br>
	<br> Question:
	<b>${questionName}</b>

	<br>
	<br> Submitted Files:
	<br>

	<c:forEach items="${submittedFiles}" var="fileName">
		
		${fileName} 
		&nbsp;&nbsp;
		<a
			href="questionStudent?studentName=${studentName}&questionName=${questionName}&file=${fileName}"
			target="_blank"> view as pdf</a>
		
		
		&nbsp;&nbsp;
		<a
			href="questionStudent?studentName=${studentName}&questionName=${questionName}&file=${fileName}&original=true"
			target="_blank"> view as original</a>
		<br>
	</c:forEach>

	<br>
	<small> * depending on your browser settings, file may open in
		new tab, new window or may download</small>
	<br>
	<br>
	
	<br> Test Case Results:
	<br>

	<c:forEach items="${codeOutput}" var="_code_output_" varStatus="loop">
		${loop.index + 1}.
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;
  		Student Output : <b>${_code_output_}</b>
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;
		Expected Output:  ${expectedOutput[loop.index]}
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;	
		Test Case <b>${testcaseResult[loop.index]}</b>
		<br>
	</c:forEach>

	<br>

	<form:form modelAttribute="gradeHomework" method="post">
	
		Enter Marks:  <form:input path="marksGiven"
			placeholder="Enter Marks for Ques. ${questionName}" />

		<form:errors path="marksGiven" cssClass="error" />

		<br>
		<br>

		<p class="formfield">
			<label for="textarea">Feedback: </label>
			<textarea id="textarea" name="feedback" rows="5"
				placeholder="Enter Feedback for Question ${questionName}">${gradeHomework.feedback}</textarea>
		</p>

		<br>
		<br>
		<input type="submit" value="SUBMIT" />

	</form:form>

	<br>
	<a href="student?studentName=${studentName}"> Click here</a> to grade
	remaining questions for
	<b>${studentName}</b>

	<br>
	<br>
	<a href="question?questionName=${questionName}"> Click here</a> to
	grade remaining students for Question
	<b>${questionName}</b>

	<br>
	<br>
	<a href="showStudentList">Click here</a> to see list of students for
	homework
	<b>${gradeHomework.homework}</b>

	<br>
	<br>
	<a href="showQuestionList">Click here</a> to see list of questions for
	homework
	<b>${gradeHomework.homework}</b>

	<br>
	<br>
	<a href="showCompletedGrading" target="_blank"> Click here</a> to see
	students whose grading is completed

	<br>
	<br>
</body>
</html>