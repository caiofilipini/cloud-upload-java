package com.caiofilipini.upload.stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.fileupload.FileItemStream;
import org.junit.Test;

import com.caiofilipini.upload.progress.InProgress;
import com.caiofilipini.upload.progress.UploadProgress;
import com.caiofilipini.upload.stream.UploadStream;

public class UploadStreamTest {

    private UploadStream uploadStream;
    private FileItemStream stream;
    private String uid;

    @Test
    public void shouldReadUploadedFileFromInputStreamAndWriteNewFileToDisk() throws Exception {
        String uploadContents = "SOMERANDOMFILECONTENT";
        setupStreamWith(uploadContents, uploadContents.length());

        uploadStream.copyToFile("/tmp/", uid);

        assertEquals(uploadContents, new BufferedReader(new FileReader("/tmp/" + uid)).readLine());
    }

    @Test
    public void shouldUpdateProgressAsChunksAreWrittenToFile() throws Exception {
        // content size is set to a fraction of the total size (1024),
        // so we are able to check for the progress in the test.
        String uploadContents = generateStringWithSize(128);
        setupStreamWith(uploadContents, 1024);

        uploadStream.copyToFile("/tmp/", uid);

        Integer expectedPercentage = 13;
        assertEquals(expectedPercentage, InProgress.now(uid).calculatePercentage());
    }

    @Test
    public void shouldUpdateProgressTo100WhenFileUploadIsComplete() throws Exception {
        String uploadContents = "This file is really tiny, but it's ok.";
        setupStreamWith(uploadContents, uploadContents.length());

        uploadStream.copyToFile("/tmp/", uid);

        UploadProgress progress = InProgress.now(uid);
        Integer expectedPercentage = 100;
        assertEquals(expectedPercentage, progress.calculatePercentage());
    }

    private void setupStreamWith(String uploadContents, Integer totalSize) throws IOException {
        this.uid = newUid();

        stream = mock(FileItemStream.class);
        when(stream.openStream()).thenReturn(new ByteArrayInputStream(uploadContents.getBytes()));

        UploadProgress progress = new UploadProgress(Long.valueOf(totalSize));
	    InProgress.store(uid, progress);

        this.uploadStream = new UploadStream(stream, progress);
	}

    private String newUid() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String generateStringWithSize(int stringSize) {
        char c = '*';
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < stringSize; i++) {
            s.append(c);
        }
        return s.toString();
    }

}
