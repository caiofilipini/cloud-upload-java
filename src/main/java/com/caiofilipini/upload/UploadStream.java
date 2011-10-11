package com.caiofilipini.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemStream;

public class UploadStream {

    private static final int CHUNK_SIZE = 512;
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
            diskFile = new FileOutputStream(new File(basePath + newFilePath));
            uploadStream = stream.openStream();
            System.out.println("Stream opened for " + newFilePath);

            byte[] chunk = new byte[CHUNK_SIZE];
            int numberOfBytesRead = 0;

            while ((numberOfBytesRead = uploadStream.read(chunk)) != -1) {
                diskFile.write(chunk, 0, numberOfBytesRead);
                progress.completedMore(numberOfBytesRead);
            }
        } catch (Exception e) {
            System.out.println("Error writing " + newFilePath);
            e.printStackTrace(System.out);
            throw e;
        } finally {
            if (uploadStream != null) {
                uploadStream.close();
            }
            if (diskFile != null) {
                diskFile.close();
            }
        }
    }

}
