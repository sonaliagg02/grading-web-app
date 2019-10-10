<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Feedback: <%= request.getParameter("studentName") %> </title>
</head>
<body>

	<h3>Feedback</h3>

	Homework: ${gradeHomework.homework}
	<br>
	<br> Student:
	<%= request.getParameter("studentName") %>

	<br>
	<br>Question:
	<%= request.getParameter("questionName") %>

	<br>
	<br> Marks: ${marks}

	<br>
	<br> Feedback: ${feedback}

	<br>
	<br>
</body>
</html>