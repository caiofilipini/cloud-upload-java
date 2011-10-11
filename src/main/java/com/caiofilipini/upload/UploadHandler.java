package com.caiofilipini.upload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public class UploadHandler extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String FILES_PATH = "/files";
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

        String path = this.servletContext.getRealPath(".") + FILES_PATH;
        File filesPath = new File(path);

        if (!filesPath.exists()) {
            System.out.println("Creating directory " + path);
            filesPath.mkdir();
            System.out.println("Directory " + path + " successfully created.");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int totalSize = request.getContentLength();
        System.out.println("Received " + totalSize + " bytes in POST.");

        Map<String, String> params = new HashMap<String, String>();

        ServletFileUpload fileUpload = getFileUpload();
        FileItemIterator itemIterator = null;

        try {
            itemIterator = fileUpload.getItemIterator(request);

            while (itemIterator.hasNext()) {
                FileItemStream item = itemIterator.next();

                if (item.isFormField()) {
                    String fieldValue = Streams.asString(item.openStream());
                    params.put(item.getFieldName(), fieldValue);
                } else {
                    String uid = params.get("uid");

                    if (uid == null || uid.isEmpty()) {
                        respond400(response);
                        return;
                    }

                    writeStreamToDisk(totalSize, item, uid, request);
                }
            }
        } catch (FileUploadException e) {
            throw new IOException(e);
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void respond400(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    private void writeStreamToDisk(
            int totalSize,
            FileItemStream stream,
            String uid,
            HttpServletRequest request) throws IOException {

        UploadProgress progress = new UploadProgress(Long.valueOf(totalSize));
        InProgress.store(uid, progress);

        String originalFileName = stream.getName();
        System.out.println("Original file name: " + originalFileName);

        String newFilePath = newFileNameFor(uid, originalFileName);
        System.out.println("New file path: " + newFilePath);

        String webappDiskPath = this.servletContext.getRealPath(".");
        System.out.println("Webapp disk path: " + webappDiskPath);

        long start = System.currentTimeMillis();
        System.out.println("Started writing " + newFilePath + "...");

        new UploadStream(stream, progress).copyToFile(webappDiskPath, newFilePath);
        long end = System.currentTimeMillis();

        String downloadablePath = request.getContextPath() + newFilePath;
        progress.complete(downloadablePath);

        System.out.println("Finished writing " + newFilePath + " in " + (end - start) + "ms.");
    }

    private String newFileNameFor(String uid, String originalFileName) {
        return FILES_PATH + File.separator + uid + extractExtensionFrom(originalFileName);
    }

    private String extractExtensionFrom(String name) {
        Pattern fileExtensionRegex = Pattern.compile("(\\.\\w+)$");
        Matcher fileExtensionMatcher = fileExtensionRegex.matcher(name);
        String extension = "";

        if (fileExtensionMatcher.find()) {
            extension = fileExtensionMatcher.group();
        }

        return extension;
    }

    private ServletFileUpload getFileUpload() {
        return this.fileUpload != null ? this.fileUpload : new ServletFileUpload();
    }

}
