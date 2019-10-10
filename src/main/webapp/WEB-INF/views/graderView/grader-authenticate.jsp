<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Authentication</title>
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
	<h3>Hello Grader!</h3>
	<br>
	<br>
	<form:form modelAttribute="grader" method="post">

	Username: <form:input path="username" type="text"
			placeholder="Enter Username" />
		<br>
		<form:errors path="username" cssClass="error" />
		<br>
		<br> 
	Password:<form:input type="password" path="password"
			placeholder="Enter Password" />
		<br>
		<form:errors path="password" cssClass="error" />
		<br>
		<br>
		<form:errors path="incorrectCredentials" cssClass="error" />
		<br>
		<br>
		<input type="submit" value="SUBMIT" />
	</form:form>


</body>
</html>