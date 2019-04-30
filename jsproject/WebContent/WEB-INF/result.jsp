<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="java.sql.*"%>
<%@page import="java.io.*, java.util.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*, java.security.*"%>
<%@ page import="controllers.DBServlet"%>




<%
	DBServlet db = new DBServlet();
	// the mysql insert statement
	String query = db.getQueryValue();
	String[][] results = null;
	try {
		results = db.getResults(query, db.getConnection());
	} catch (SQLException e) {
		e.printStackTrace();
	}
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>


h1{
  font-size: 30px;
  color: #fff;
  text-transform: uppercase;
  font-weight: 300;
  text-align: center;
  margin-bottom: 15px;
}
table{
  width:100%;
  table-layout: fixed;
}
.tbl-header{
  background-color: rgba(255,255,255,0.3);
 }
.tbl-content{
  height:300px;
  overflow-x:hidden;
  margin-top: 0px;
  border: 0px solid rgba(255,255,255,0.3);
}
th{
  padding: 20px 15px;
  max-width: 150px;
  word-wrap: break-word;
  text-align: left;
  font-weight: 500;
  font-size: 12px;
  color: #fff;
  text-transform: uppercase;
}
td{
  max-width: 150px;
  word-wrap: break-word;
  padding: 15px;
  text-align: left;
  vertical-align:middle;
  font-weight: 300;
  font-size: 12px;
  color: #fff;
  border-bottom: solid 1px rgba(255,255,255,0.1);
}


/* demo styles */

@import url(https://fonts.googleapis.com/css?family=Roboto:400,500,300,700);
body{
  background: -webkit-linear-gradient(left, #25c481, #25b7c4);
  background: linear-gradient(to right, #0085ff, #0085ff);
  font-family: 'Roboto', sans-serif;
}
section{
  margin:50px;
}

</style>
<script>
$(window).on("load resize ", function() {
	  var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
	  $('.tbl-header').css({'padding-right':scrollWidth});
	}).resize();
</script>
</head>
<body>
	<a href="http://localhost:8080/jsproject/index.jsp">Home<a/>

	<%
		if (results != null) {
	%>
	<div class="tbl-header">
    <table cellpadding="0" cellspacing="0" border="0">
      <thead>
		<tr>

			<%
				for (int i = 0; i < db.getColumnCount(); i++) {
			%>

			<th><%=results[0][i]%></th>

			<%
				}
			%>

		</tr>
		</thead>
		</table>
  </div>
  <div class="tbl-content">
    <table cellpadding="0" cellspacing="0" border="0">
      <tbody>
		<!--  try to dump the values into an array and loop the array since resultset being a bitch -->
		<%
			for (int r = 1; r < results.length; r++) {
		%>

		<tr>
			<%
				for (int c = 0; c < results[r].length; c++) {
			%>
			<td><%=results[r][c]%></td>

			<%
					}
			%>
		</tr>
		<%
				}
			} else {

		%> <b>That query has failed!</b> 
		<% } %>

</tbody>
	</table>
</div>

</body>
</html>


