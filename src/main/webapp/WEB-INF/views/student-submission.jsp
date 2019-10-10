<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<title>Student Submission</title>

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
	<h3>ALGORITHMS-GRADING</h3>
	<p>All the fields are required</p>
	<form:form modelAttribute="submission" method="post">
	Username: <form:input path="username"
			placeholder="Enter RIT CS Username" />
		<form:errors path="username" cssClass="error" />
		<br>
		
		<br>Homework:
		<form:select path="homework">
			<form:options items="${hw.homeworkOptions}" />
		</form:select>
		<form:errors path="homework" cssClass="error" />


		<br>
		<br>

		<input type="submit" value="SUBMIT" />

		<br>
		<br>		
		<form:errors path="date" cssClass="error" />
	</form:form>

	Part 1/2
</body>
</html>