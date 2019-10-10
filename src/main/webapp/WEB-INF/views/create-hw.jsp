<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create Homework</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="/resources/demos/style.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script type="text/javascript">
  $(function() {
      $('#DateField').datepicker();
  });
  </script>

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
	<h3>Create New Homework</h3>
	<br>
	<br>
	<form:form modelAttribute="homework" method="post">
		
 	Homework name: <form:input path="id"
			placeholder="Enter new homework number" />
		<form:errors path="id" cssClass="error" />
		<br>
		<br>

	Number of Questions: <form:input path="numberOfQuestions"
			placeholder="Enter number of questions" />
		<form:errors path="numberOfQuestions" cssClass="error" />
		<br>
		<br>
	Due Date (mm/dd/yyyy): <form:input id="DateField" path="dueDate" />
		<form:errors path="dueDate" cssClass="error" />
		<br>
		<br>
	* use the calendar to select the date. 
		<br>
		<br>
		<br>

		<input type="submit" value="SUBMIT" />
	</form:form>

</body>
</html>