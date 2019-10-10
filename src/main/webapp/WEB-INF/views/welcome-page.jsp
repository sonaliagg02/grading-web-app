<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome</title>
</head>
<body>

	<h3>ALGORITHMS-GRADING</h3>
	<form method="get" action="student/showForm">
		<input type="submit" value="Student" />
	</form>
	<br>
	<br>
	<br>
	<form method="get" action="professor/authenticate">
		<input type="submit" value="Professor" />
	</form>
	<br>
	<br>
	<br>
	
	<form method="get" action="grader/authenticate">
		<input type="submit" value="Grader" />
	</form>
	<br>
	<br>
	<br>

</body>
</html>