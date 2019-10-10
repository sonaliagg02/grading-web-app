<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Student Submission</title>
<style>
.error {
	color: red
}
</style>

</head>
<body>
	<h3>ALGORITHMS-GRADING</h3>
	<p>All the fields are required</p>
	<form:form modelAttribute="submission" method="post"
		enctype="multipart/form-data">
		
	RIT CS Username: <b>${submission.username}</b>
		<br>
		<br>
 	 Homework: <b> ${submission.homework} </b>
		<br>
		<br>

		Question Name:
		<form:select path="problemName">
			<form:options items="${hw.questionOptions}" />
		</form:select>
		<form:errors path="problemName" cssClass="error" />
		<br>
		<br>
		
	Code for the Question: 
		<small>(*Max 1.5 MB combined) <br> Allowed extensions: <c:forEach
				items="${codeExtension}" var="ext"> ${ext} </c:forEach>
		</small>
		<br>
		<form:input path="codeFiles" type="file" multiple="multiple" />
		<form:errors path="codeFiles" cssClass="error" />
		<br>
		<br>
 	Language:<form:radiobuttons path="language" items="${langaugeOptions}" />
		<form:errors path="language" cssClass="error" />
		<br>
		<br>

	Write-Up for the Question:
		<small> Allowed extensions: <c:forEach
				items="${writeupExtension}" var="ext"> ${ext} </c:forEach>
		</small>
		<br>
		<form:input path="writeupFiles" type="file" />
		<form:errors path="writeupFiles" cssClass="error" />
		<br>
		<br>

		<input type="submit" value="SUBMIT" />
	</form:form>
	<br>
	<br>
	<small>** IF VALIDATION ERROR OCCURS CODE AND WRITE-UP FILES
		NEED TO BE UPLOADED AGAIN</small>
	<br>
	<br>
	<br> Part 2/2
</body>
</html>