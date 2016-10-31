<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!doctype html>
<%@ page import="it.sauronsoftware.cron4j.TaskExecutor"%>
<%
    // Retrieves the current timestamp
    long now = System.currentTimeMillis();
// Retrieves the executors.
    //TaskExecutor[] executors = (TaskExecutor[]) request.getAttribute("executors");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Barchart Ddf Service</title>
</head>
<body>
<h1>Ongoing tasks</h1>


<p>Timestamp: <%= now %></p>
<p><a href="?ts=<%= now %>" onclick="location.reload(); return false;">Refresh page</a></p>
</body>
</html>