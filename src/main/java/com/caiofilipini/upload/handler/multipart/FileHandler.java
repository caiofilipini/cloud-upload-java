package com.caiofilipini.upload.handler.multipart;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caiofilipini.upload.progress.InProgress;
import com.caiofilipini.upload.progress.UploadProgress;
import com.caiofilipini.upload.stream.UploadStream;

public class FileHandler {

    private static Logger log = LoggerFactory.getLogger(FileHandler.class);
    private final HttpServletRequest request;
    private final String webappDiskPath;
    private final String filesPath;

    public FileHandler(HttpServletRequest request, String webappDiskPath, String filesPath) {
        this.request = request;
        this.webappDiskPath = webappDiskPath;
        this.filesPath = filesPath;
    }

    public int handle(FileItemStream uploadedFile, String uid) {
        if (isEmpty(uid)) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }

        int totalSize = request.getContentLength();

        UploadProgress progress = new UploadProgress(Long.valueOf(totalSize));
        InProgress.store(uid, progress);

        String newFilePath = newFileNameFor(uid, uploadedFile.getName());

        try {
            writeStreamToDisk(uploadedFile, newFilePath, progress);
            return HttpServletResponse.SC_OK;
        } catch (IOException e) {
            log.error("An error occurred while handling upload id {}", uid);

            InProgress.abort(uid);

            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    private void writeStreamToDisk(
            FileItemStream multipartField,
            String newFilePath,
            UploadProgress progress) throws IOException {
        long start = System.currentTimeMillis();
        log.info("Started writing {}", newFilePath);

        new UploadStream(multipartField, progress).copyToFile(webappDiskPath, newFilePath);

        String downloadablePath = request.getContextPath() + newFilePath;
        progress.fileAvailableAt(downloadablePath);

        long end = System.currentTimeMillis();
        log.info("Finished writing {} in {} ms.", newFilePath, (end - start));
    }

    private String newFileNameFor(String uid, String originalFileName) {
        return filesPath + File.separator + uid + extractExtensionFrom(originalFileName);
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

    private boolean isEmpty(String uid) {
        return uid == null || uid.isEmpty();
    }

}
