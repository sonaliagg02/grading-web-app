<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Homework Uploaded</title>
</head>
<body>
	<h3>ALGORITHMS-GRADING</h3>
	<br>
	<br> Question Added to homework ${homework.id}

	<br>
	<br>
	<b>Question Details</b>
	<br> Question number: ${question.questionNumber}
<%-- 	<br> Question Description: ${question.questionDescription} --%>
	<br> Problem Name: ${question.problemName}
	<br> test case inputs:
	<br>
	<c:forEach items="${question.testCaseNames}" var="fileName">
		File <b>${fileName}</b> uploaded successfully<br />
	</c:forEach>

	<br> test case outputs:
	<br>
	<c:forEach items="${question.outputTestCaseNames}" var="fileName">
		File <b>${fileName}</b> uploaded successfully<br />
	</c:forEach>
	<br>
	<br>

	<b>Homework Details</b>
	<br> Homework Name: ${homework.id}
	<br> Number of Questions: ${homework.numberOfQuestions}
	<br> Due Date: ${homework.dueDate}

	<br>
	<br> Uploaded questions:${currentQuestion-1} /
	${homework.numberOfQuestions}
	<c:choose>
		<c:when test="${currentQuestion == homework.numberOfQuestions + 1}">

			<br>
			<form:form method="get" action="logout">
				<input type="submit" value="Logout" />
			</form:form>
		</c:when>

		<c:otherwise>
			<br>
			<form:form method="get" action="createHomeworkNextQuestion">
				<input type="submit" value="Upload Question ${currentQuestion}" />
			</form:form>
		</c:otherwise>
	</c:choose>

</body>
</html>