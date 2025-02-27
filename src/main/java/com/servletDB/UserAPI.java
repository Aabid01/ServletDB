package com.servletDB;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(
        urlPatterns = "/initServlet",
        initParams = {
                @WebInitParam(name = "message", value = "Hello from @WebInitParam!"),
                @WebInitParam(name = "version", value = "1.0")
        }
)
public class UserAPI extends HttpServlet {
    private String claasVariableMessage;
    private String classVariableVersion;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        claasVariableMessage = config.getInitParameter("message");
        classVariableVersion = config.getInitParameter("version");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        response.getWriter().println("<h1>" + claasVariableMessage + "</h1>");
        response.getWriter().println("<h6>" + classVariableVersion + "</h6>");
    }

}