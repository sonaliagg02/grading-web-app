<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Success</title>
<style>
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
	Code For <b>${submission.problemName}</b>:
	<br>

	<c:forEach items="${codeFileNames}" var="fileName">
		File <b>${fileName}</b> uploaded successfully<br />
	</c:forEach>

	<br>

	<br> Write-up For <b>${submission.problemName}</b>:

	<br>

	<c:forEach items="${writeupFileNames}" var="fileName">
		File <b>${fileName}</b> uploaded successfully<br />
	</c:forEach>

	<br>

	<c:forEach items="${outputList}" var="output" varStatus="loop">
		${loop.index + 1}.
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;
  		Your Output : <b>${output}</b>
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;
		Expected Output:  ${expectedOutput[loop.index]}
		
		&nbsp;&nbsp;&nbsp;&nbsp;	&nbsp;&nbsp;&nbsp;&nbsp;	
		Test Case <b>${codeStatus[loop.index]}</b>
		<br>
	</c:forEach>

	<br>

	<form method="post">
		<input type="submit" value="Submit Code for Another Question" />
	</form>

	<form method="post" action="redirectHome">
		<input type="submit" value="Logout" />
	</form>
</body>
</html>

