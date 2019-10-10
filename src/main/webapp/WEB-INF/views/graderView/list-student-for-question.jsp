<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Student List for ${questionName}</title>
</head>
<body>



	<br> Homework:
	<b>${gradeHomework.homework}</b>

	<br>
	<br> List of Students for Question
	<b>${questionName}</b>:
	<br>

	<c:forEach items="${studentListForQuestion}" var="student">
		<a
			href="questionStudent?questionName=${questionName}&studentName=${student}">${student}</a>
		<br />
	</c:forEach>

	<br>
	<br>
	<a href="showCompletedGrading" target="_blank"> Click here</a> to see
	students whose grading is completed
	
</body>
</html>