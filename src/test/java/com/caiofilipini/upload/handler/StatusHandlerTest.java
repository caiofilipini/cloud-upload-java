package com.caiofilipini.upload.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.caiofilipini.upload.handler.StatusHandler;
import com.caiofilipini.upload.progress.InProgress;
import com.caiofilipini.upload.progress.UploadProgress;

public class StatusHandlerTest {

    private StatusHandler statusHandler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter responseWriter;

    @Before
    public void createSubject() {
        statusHandler = new StatusHandler();
    }

    @Before
    public void createRequestAndResponse() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        responseWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(responseWriter);
    }

    @Test
    public void shouldRespond404() throws Exception {
        whenUidIsNull();
        whenUidIsEmpty();
        whenUidContainsOnlyWhitespaces();
        whenNoUploadProgressIsFoundForUid();
    }

    @Test
    public void shouldRespond200WithPercentageAsJsonWhenUploadProgressIsAvailableForUid() throws Exception {
        String uid = String.valueOf(System.currentTimeMillis());
        InProgress.store(uid, new UploadProgress(1024L, 102L));
        when(request.getParameter("uid")).thenReturn(uid);

        statusHandler.doGet(request, response);

        verify(response).setStatus(200);
        verify(responseWriter).write("{\"completed\" : \"10\"}");
    }

    public void whenUidIsNull() throws Exception {
        when(request.getParameter("uid")).thenReturn(null);
        verify404WasSent();
        reset();
    }

    public void whenUidIsEmpty() throws Exception {
        when(request.getParameter("uid")).thenReturn("");
        verify404WasSent();
        reset();
    }

    public void whenUidContainsOnlyWhitespaces() throws Exception {
        when(request.getParameter("uid")).thenReturn("     ");
        verify404WasSent();
        reset();
    }

    public void whenNoUploadProgressIsFoundForUid() throws Exception {
        when(request.getParameter("uid")).thenReturn("41");
        verify404WasSent();
        reset();
    }

    private void verify404WasSent() throws ServletException, IOException {
        statusHandler.doGet(request, response);
        verify(response).sendError(404);
    }

    private void reset() {
        Mockito.reset(request, response, responseWriter);
    }

}
