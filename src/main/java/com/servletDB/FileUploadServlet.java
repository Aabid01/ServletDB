package com.servletDB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class FileUploadServlet extends HttpServlet {
    private static final String UPLOADDIR = "upload";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOADDIR;
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            System.out.println("Trying to create a dir.");
            uploadDir.mkdir();
        }

        Part filePart = req.getPart("file");
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
        resp.getWriter().println("File Uplaod Successfully : " + fileName);
    }
}
