package com.caiofilipini.upload.handler;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caiofilipini.upload.handler.multipart.MultipartRequestHandler;

public class UploadHandler extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String FILES_PATH = "/files";
    private static Logger log = LoggerFactory.getLogger(UploadHandler.class);

    private ServletFileUpload fileUpload;
    private ServletContext servletContext;

    public UploadHandler() {
    }

    UploadHandler(ServletFileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.servletContext = config.getServletContext();
        configureFilesPath();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("Received {} bytes through POST.", request.getContentLength());

        String webappDiskPath = this.servletContext.getRealPath(".");
        MultipartRequestHandler multipartHandler = new MultipartRequestHandler(request, getFileUpload());

        int responseStatus = multipartHandler.handleMultipartFields(webappDiskPath, FILES_PATH);
        response.setStatus(responseStatus);
    }

    private void configureFilesPath() {
        String path = this.servletContext.getRealPath(".") + FILES_PATH;
        File filesPath = new File(path);

        if (!filesPath.exists()) {
            log.info("Creating directory {}", path);

            filesPath.mkdir();

            log.info("Directory {} successfully created.", path);
        }
    }

    private ServletFileUpload getFileUpload() {
        return this.fileUpload != null ? this.fileUpload : new ServletFileUpload();
    }

}
