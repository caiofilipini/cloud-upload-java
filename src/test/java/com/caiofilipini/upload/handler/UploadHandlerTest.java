package com.caiofilipini.upload.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;

import com.caiofilipini.upload.handler.UploadHandler;
import com.caiofilipini.upload.progress.InProgress;
import com.caiofilipini.upload.progress.ProgressNotFoundException;
import com.caiofilipini.upload.progress.UploadProgress;

public class UploadHandlerTest {

    private ServletFileUpload fileUpload;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UploadHandler uploadHandler;
    private FileItemStream uploadStream;
    private String uid;

    @Before
    public void createSubject() throws Exception {
        ServletContext servletContext = mock(ServletContext.class);

        when(servletContext.getRealPath(anyString())).thenReturn("/tmp");

        ServletConfig config = mock(ServletConfig.class);
        when(config.getServletContext()).thenReturn(servletContext);

        fileUpload = mock(ServletFileUpload.class);

        uploadHandler = new UploadHandler(fileUpload);
        uploadHandler.init(config);

        uid = newUid();
    }

    @Before
    public void createRequestAndResponse() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void shouldReadUploadedFileFromMultipartRequestAndWriteNewFileToDisk() throws Exception {
        String uploadContents = "SOMERANDOMFILECONTENT";
        setupMultiPartRequest("randomfile.txt", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);

        assertEquals(uploadContents, new BufferedReader(new FileReader("/tmp/files/" + uid + ".txt")).readLine());
    }

    @Test
    public void shouldMakeFilePathAvailableWhenFileUploadIsComplete() throws Exception {
        String uploadContents = "This file should be available when upload is completed.";
        setupMultiPartRequest("somefile.txt", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);
        UploadProgress progress = InProgress.now(uid);

        assertNotNull(progress.getFilePath());
        assertTrue(progress.getFilePath().contains(uid));
    }

    @Test
    public void shouldUseOriginalFilesExtensionInTheNewFileName() throws Exception {
        String uploadContents = "This file should be available when upload is completed.";
        setupMultiPartRequest("my_awesome_set.mp3", uid, uploadContents, uploadContents.length());

        uploadHandler.doPost(request, response);
        UploadProgress progress = InProgress.now(uid);

        assertTrue(progress.getFilePath().endsWith(uid + ".mp3"));
    }

    @Test
    public void shouldNotIncludeExtensionInTheNewFileNameIfTheOriginalOneLacksExtension() throws Exception {
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

        verify(response).setStatus(400);
        verify(uploadStream, never()).openStream();
    }

    @Test(expected = ProgressNotFoundException.class)
    public void shouldRemoveProgressAndRespond500IfCannotWriteFile() throws Exception {
        String uploadContents = "This file will never be written to disk.";
        setupMultiPartRequest("error.txt", uid, uploadContents, uploadContents.length());
        // simulating an IOException
        when(uploadStream.openStream()).thenThrow(new IOException());

        uploadHandler.doPost(request, response);

        verify(response).setStatus(500);
        verify(response, never()).setStatus(200);
        // this should throw a ProgressNotFoundException
        InProgress.now(uid);
    }

    private String newUid() {
        return String.valueOf(System.currentTimeMillis());
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

        when(request.getContextPath()).thenReturn("/");
        when(request.getContentLength()).thenReturn(contentLength);
    }

    private ByteArrayInputStream stringToStream(String string) {
        return new ByteArrayInputStream(string.getBytes());
    }

}
