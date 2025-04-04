package com.servletDB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/user")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class UserManagementServlet extends HttpServlet {

    // JDBC Configuration
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/usermanagementservlet";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "qwerty";

    // Static block to load the MySQL JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load the MySQL JDBC Driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Failed to load MySQL driver");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS)) {
                String query = "SELECT * FROM users WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, Integer.parseInt(idParam));
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        response.setContentType("application/json");
                        PrintWriter out = response.getWriter();
                        out.println("{");
                        out.println("\"id\": " + resultSet.getInt("id") + ",");
                        out.println("\"name\": \"" + resultSet.getString("name") + "\",");
                        out.println("\"email\": \"" + resultSet.getString("email") + "\",");
                        out.println("\"address\": \"" + resultSet.getString("address") + "\",");
                        out.println("\"gender\": \"" + resultSet.getString("gender") + "\",");
                        out.println("\"age\": " + resultSet.getInt("age") + ",");
                        out.println("\"picture\": \"" + resultSet.getString("picture") + "\"");
                        out.println("}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().println("User not found");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing user ID");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");
        int age = Integer.parseInt(request.getParameter("age"));

        // Handle file upload (picture)
        String UPLOADDIR = "upload";
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOADDIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            System.out.println("Trying to create a dir.");
            uploadDir.mkdir();
        }

        Part filePart = request.getPart("picture");
        String fileName = filePart.getSubmittedFileName();

        try (InputStream input = filePart.getInputStream();
             FileOutputStream output = new FileOutputStream(uploadDir + File.separator + fileName)

        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }

//        Part picturePart = request.getPart("picture");
//        InputStream pictureStream = picturePart.getInputStream();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS)) {
            String query = "INSERT INTO users (name, email, address, gender, age, picture) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, address);
                statement.setString(4, gender);
                statement.setInt(5, age);
                statement.setString(6, fileName);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.getWriter().println("User created successfully");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String address = request.getParameter("address");
            String gender = request.getParameter("gender");
            int age = Integer.parseInt(request.getParameter("age"));

            // Handle file upload (picture)
            Part picturePart = request.getPart("picture");
            InputStream pictureStream = picturePart.getInputStream();

            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS)) {
                String query = "UPDATE users SET name = ?, email = ?, address = ?, gender = ?, age = ?, picture = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, name);
                    statement.setString(2, email);
                    statement.setString(3, address);
                    statement.setString(4, gender);
                    statement.setInt(5, age);
                    statement.setBlob(6, pictureStream);
                    statement.setInt(7, id);
                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        response.getWriter().println("User updated successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().println("User not found");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing user ID");
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS)) {
                String query = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, id);
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        response.getWriter().println("User deleted successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().println("User not found");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Missing user ID");
        }
    }
}
