package com.caiofilipini.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UploadHandlerTest {

    private ServletFileUpload fileUpload;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UploadHandler uploadHandler;
    private FileItemStream uploadStream;

    @BeforeClass
    public static void createTempFilesPath() {
        new File("/tmp/files").mkdir();
    }

    @Before
    public void createSubject() {
        fileUpload = mock(ServletFileUpload.class);
        uploadHandler = new UploadHandler(fileUpload);
    }

    @Before
    public void createRequestAndResponse() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void shouldReadUploadedFileFromMultipartRequestAndWriteNewFileToDisk() throws Exception {
        String uid = String.valueOf(System.currentTimeMillis());
        String uploadContents = "SOMERANDOMFILECONTENT";
        setupMultiPartRequest("randomfile.txt", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);

        assertEquals(uploadContents, new BufferedReader(new FileReader("/tmp/files/" + uid + ".txt")).readLine());
    }

    @Test
    public void shouldUpdateProgressTo100WhenFileUploadIsComplete() throws Exception {
        String uid = String.valueOf(System.currentTimeMillis());
        String uploadContents = "This file is really tiny, but it's ok.";
        setupMultiPartRequest("tinyfile.txt", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);
        UploadProgress progress = InProgress.now(uid);

        Integer expectedPercentage = 100;
        assertEquals(expectedPercentage, progress.calculatePercentage());
    }

    @Test
    public void shouldMakeFilePathAvailableWhenFileUploadIsComplete() throws Exception {
        String uid = String.valueOf(System.currentTimeMillis());
        String uploadContents = "This file should be available when upload is completed.";
        setupMultiPartRequest("somefile.txt", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);
        UploadProgress progress = InProgress.now(uid);

        assertNotNull(progress.getFilePath());
        assertTrue(progress.getFilePath().contains(uid));
    }

    @Test
    public void shouldUpdateProgressAsChunksAreWrittenToFile() throws Exception {
        String uid = String.valueOf(System.currentTimeMillis());
        String uploadContents = generateStringWithSize(128);
        // content-length is set to a fraction of the size of the actual content,
        // so we are able to check for the progress in the test.
        setupMultiPartRequest("anything.txt", uid, uploadContents, 1024);

        uploadHandler.doPost(request, response);
        UploadProgress progress = InProgress.now(uid);

        Integer expectedPercentage = 13;
        assertEquals(expectedPercentage, progress.calculatePercentage());
    }

    @Test
    public void shouldUseOriginalFilesExtensionInTheNewFileName() throws Exception {
        String uid = String.valueOf(System.currentTimeMillis());
        String uploadContents = "This file should be available when upload is completed.";
        setupMultiPartRequest("my_awesome_set.mp3", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);
        UploadProgress progress = InProgress.now(uid);

        assertTrue(progress.getFilePath().endsWith(uid + ".mp3"));
    }

    @Test
    public void shouldNotIncludeExtensionInTheNewFileNameIfTheOriginalOneLacksExtension() throws Exception {
        String uid = String.valueOf(System.currentTimeMillis());
        String uploadContents = "This file should be available when upload is completed.";
        setupMultiPartRequest("my_awesome_set_without_extension", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);
        UploadProgress progress = InProgress.now(uid);

        assertTrue(progress.getFilePath().endsWith(uid));
    }

    @Test
    public void shouldRespond400AndNeverOpenUploadStreamIfUidIsNotPresentInMultipartRequest() throws Exception {
        String emptyUid = "";
        String ignoredUpload = "ignore me";
        setupMultiPartRequest(null, emptyUid, ignoredUpload, ignoredUpload.length());

        uploadHandler.doPost(request, response);

        verify(response).sendError(400);
        verify(uploadStream, never()).openStream();
    }

    private void setupMultiPartRequest(String originalFileName, String uid, String uploadContents, int contentLength) throws Exception {
        FileItemStream uidStream = mock(FileItemStream.class);
        when(uidStream.isFormField()).thenReturn(true);
        when(uidStream.getFieldName()).thenReturn("uid");
        when(uidStream.openStream()).thenReturn(stringToStream(uid));

        uploadStream = mock(FileItemStream.class);
        when(uploadStream.isFormField()).thenReturn(false);
        when(uploadStream.getName()).thenReturn(originalFileName);
        when(uploadStream.openStream()).thenReturn(stringToStream(uploadContents));

        FileItemIterator itemIterator = mock(FileItemIterator.class);
        when(itemIterator.hasNext()).thenReturn(true, true, false);
        when(itemIterator.next()).thenReturn(uidStream, uploadStream);
        when(fileUpload.getItemIterator(request)).thenReturn(itemIterator);

        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/");
        when(servletContext.getRealPath(anyString())).thenReturn("/tmp");

        when(request.getServletContext()).thenReturn(servletContext);
        when(request.getContentLength()).thenReturn(contentLength);
    }

    private String generateStringWithSize(int stringSize) {
        char c = '*';
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < stringSize; i++) {
            s.append(c);
        }
        return s.toString();
    }

    private ByteArrayInputStream stringToStream(String string) {
        return new ByteArrayInputStream(string.getBytes());
    }

}
