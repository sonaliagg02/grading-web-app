<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Graded Students</title>
</head>
<body>

	<h3>List of Graded submissions for ${gradeHomework.homework}</h3>

	<br>
	<c:forEach items="${studentList}" var="studentName" varStatus="loop">
		
		${studentName}
		<br>

		<c:forEach items="${questionListForStudent[loop.index]}"
			var="questionNumber" varStatus="innerLoop">
					
				&nbsp;&nbsp;&nbsp;&nbsp; ${questionNumber} 
				
				&nbsp;&nbsp;&nbsp;&nbsp; Marks: ${marksListForStudent[loop.index][innerLoop.index]}	
				
				&nbsp;&nbsp;&nbsp;&nbsp; 
				<a
				href="Feedback?studentName=${studentName}&questionName=${questionNumber}"
				target="_blank">Feedback</a>
			<br>
		</c:forEach>

		<br>
	</c:forEach>


</body>
</html>