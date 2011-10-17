package com.caiofilipini.upload.handler;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.caiofilipini.upload.handler.FormHandler;

public class FormHandlerTest {

    private HttpServletResponse response;
    private HttpServletRequest request;
    private FormHandler formHandler;

    @Before
    public void createSubject() {
        formHandler = new FormHandler();
    }

    @Before
    public void createRequestAndResponse() {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        response = mock(HttpServletResponse.class);
        request = mock(HttpServletRequest.class);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void shouldGenerateUidAndForwardToForm() throws Exception {
        formHandler.doGet(request, response);

        verify(request).setAttribute(eq("uid"), isNotNull());
        verify(request).getRequestDispatcher("WEB-INF/jsp/uploadForm.jsp");
    }

}
