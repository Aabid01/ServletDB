package com.servletDB;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/employee")
public class EmployeeAPI extends HttpServlet {

    //DB credentials
    private String dbUrl = "jdbc:mysql://localhost:3306/empdb?createDatabaseIfNotExist=true";
    private String dbUser = "root";
    private String dbPassword = "qwerty";

    private Connection getDbConnection() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception e) {
            System.out.println("1 An exception occurred: " + e.getMessage());
        }

        return connection;
    }

    private Statement getStatement() {
        Statement statement = null;

        try {
            statement = getDbConnection().createStatement();
        } catch (SQLException e) {
            System.out.println("2 An exception occurred: " + e.getMessage());
        }

        return statement;
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = request.getParameter("id");
        String sqlQuery = "SELECT * FROM employee WHERE id = " + id;

        try {
            ResultSet resultSet = getStatement().executeQuery(sqlQuery);
            String jsonResponse = null;
            while (resultSet.next()) {
                int employeeId = resultSet.getInt("id");
                String employeeName = resultSet.getString("name");
                String employeeDepartment = resultSet.getNString("department");
                String employeeEmail = resultSet.getString("email");
                String employeeContact = resultSet.getString("contact");
                String employeeAddress = resultSet.getString("address");
                int employeeSalary = resultSet.getInt("salary");
                String employeeStatus = resultSet.getString("status");
                jsonResponse =
                        "{" +
                                "\"id\": " + employeeId + "," +
                                "\"name\": \"" + employeeName + "\"," +
                                "\"department\": \"" + employeeDepartment + "\"," +
                                "\"email\": \"" + employeeEmail + "\"," +
                                "\"contact\": \"" + employeeContact + "\"," +
                                "\"address\": \"" + employeeAddress + "\"," +
                                "\"salary\": " + employeeSalary + "," +
                                "\"status\": \"" + employeeStatus + "\"" +
                                "}";
            }


            response.setContentType("application/json");

            PrintWriter out = response.getWriter();
            out.println(jsonResponse);

        } catch (SQLException e) {
            System.out.println("3 An exception occurred: " + e.getMessage());
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");

        String name = request.getParameter("name");
        String email = request.getParameter("email");

        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Employee</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p> Name: " + name + "</p>");
        out.println("<p> Email: " + email + "</p>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain");

        String id = request.getParameter("id");
        String newData = request.getParameter("data");

        PrintWriter out = response.getWriter();
        out.println("Updated record with id: " + id);
        out.println("New Data: " + newData);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain");

        String id = request.getParameter("id");

        PrintWriter out = response.getWriter();
        out.println("Employee removed: " + id);
    }

}