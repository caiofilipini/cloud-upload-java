package com.caiofilipini.upload.handler.multipart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public class MultipartRequestHandler {

    private final HttpServletRequest request;
    private final ServletFileUpload fileUpload;
    private final Map<String, String> params;

    public MultipartRequestHandler(HttpServletRequest request, ServletFileUpload fileUpload) {
        this.request = request;
        this.fileUpload = fileUpload;
        this.params = new HashMap<String, String>();
    }

    public int handleMultipartFields(String webappDiskPath, String filesPath) throws IOException {
        int statusCode = HttpServletResponse.SC_OK;

        try {
            FileItemIterator multipartFields = fileUpload.getItemIterator(request);

            while (multipartFields.hasNext()) {
                FileItemStream multipartField = multipartFields.next();

                if (multipartField.isFormField()) {
                    extractParameter(multipartField, params);
                } else {
                    FileHandler fileHandler = new FileHandler(request, webappDiskPath, filesPath);
                    statusCode = fileHandler.handle(multipartField, params.get("uid"));
                }
            }
        } catch (FileUploadException e) {
            throw new IOException(e);
        }

        return statusCode;
    }
    
    private void extractParameter(FileItemStream multipartField, Map<String, String> params)
            throws IOException {
        String fieldValue = Streams.asString(multipartField.openStream());
        params.put(multipartField.getFieldName(), fieldValue);
    }

}
