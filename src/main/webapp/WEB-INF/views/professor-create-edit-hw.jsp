<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Hello Professor</title>
</head>
<body>
	<h2>Hello Professor</h2>
	<br>
	<br>
	<br>
	<form:form action="createNewHomework" method="get">
	Click button to create new homework: <input type="submit"
			value="Create new Homework" />
	</form:form>
	<br>
	<br>
	<%-- 	<form:form action="editHomework">
	Click button to edit homework: <input type="submit" value="Edit Homework" />
	</form:form>
 --%>

</body>
</html>