<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="java.sql.Connection"%>
<%@page import="java.sql.*"%>
<%@page import="java.sql.DriverManager"%>
<%@ page import="controllers.DBServlet"%>




<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<style>
.container { 
  height: 600px;
  position: relative;
}

.center {
  margin: 0;
  position: absolute;
  top: 50%;
  left: 50%;
  -ms-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
}

::placeholder { /* Chrome, Firefox, Opera, Safari 10.1+ */
  color: grey;
  opacity: 0.5; /* Firefox */
  font-size:15px;
}

html { 
  background: url(background.jpg) no-repeat center center fixed; 
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
}

</style>
</head>
<body style="width:100%; height:100%;">

<div class="container">
<div class="center">
<form action="DBServlet" method="POST">
<input type="text" name="champName" style="width:400px; height:30px;" placeholder="Nasus"/>
</form>
</div>
</div>
</body>
</html>





