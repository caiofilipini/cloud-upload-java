package com.caiofilipini.upload.stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caiofilipini.upload.progress.UploadProgress;

public class UploadStream {

    private static final int CHUNK_SIZE = 512;
    private static Logger log = LoggerFactory.getLogger(UploadStream.class);
    private final FileItemStream stream;
    private final UploadProgress progress;

    public UploadStream(FileItemStream stream, UploadProgress progress) {
        this.stream = stream;
        this.progress = progress;
    }

    public void copyToFile(String basePath, String newFilePath) throws IOException {
        FileOutputStream diskFile = null;
        InputStream uploadStream = null;
        
        try {
            uploadStream = stream.openStream();
            diskFile = new FileOutputStream(new File(basePath + newFilePath));
            log.debug("Upload stream opened for {}", newFilePath);

            byte[] chunk = new byte[CHUNK_SIZE];
            int numberOfBytesRead = 0;

            while ((numberOfBytesRead = uploadStream.read(chunk)) != -1) {
                diskFile.write(chunk, 0, numberOfBytesRead);
                progress.completedMore(numberOfBytesRead);
            }
        } catch (IOException e) {
            log.error("Error writing {}", newFilePath, e);
            throw e;
        } finally {
            if (diskFile != null) {
                diskFile.close();
            }
            if (uploadStream != null) {
                uploadStream.close();
                log.debug("Upload stream closed for {}", newFilePath);
            }
        }
    }

}
