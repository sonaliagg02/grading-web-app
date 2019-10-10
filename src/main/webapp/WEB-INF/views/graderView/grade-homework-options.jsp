<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Grading Homework</title>
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
</style>
</head>
<body>
	<h3>List of Homework</h3>

	<br>
	<br>
	<form:form modelAttribute="gradeHomework" method="post">
	Homework:
	<form:select path="homework">
			<form:options items="${hw.homeworkOptions}" />
		</form:select>

		<form:errors path="homework" cssClass="error" />
		<br>
		<br>
		<input type="submit" name="listByStudent"
			value="Grade Homework (List by Student)" />
		<br>
		<br>
		<input type="submit" name="listByQuestion"
			value="Grade Homework (List by Question)" />


	</form:form>
</body>
</html>