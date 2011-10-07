package com.caiofilipini.upload;

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
	public void shouldRespond404WhenUidIsNull() throws Exception {
		when(request.getParameter("uid")).thenReturn(null);
		verify404WasSent();
	}
	
	@Test
	public void shouldRespond404WhenUidIsEmpty() throws Exception {
		when(request.getParameter("uid")).thenReturn("");
		verify404WasSent();
	}
	
	@Test
	public void shouldRespond404WhenUidContainsOnlyWhitespaces() throws Exception {
		when(request.getParameter("uid")).thenReturn("     ");
		verify404WasSent();
	}
	
	@Test
	public void shouldRespond404WhenNoUploadProgressIsFoundForUid() throws Exception {
		when(request.getParameter("uid")).thenReturn("41");
		verify404WasSent();
	}
	
	@Test
	public void shouldRespond200WithPercentageAsJsonWhenUploadProgressIsAvailableForUid() throws Exception {
		String uid = String.valueOf(System.currentTimeMillis());
		InProgress.start(uid, new UploadProgress(1024L, 102L));
		when(request.getParameter("uid")).thenReturn(uid);
		
		statusHandler.doGet(request, response);
		
		verify(response).setStatus(200);
		verify(responseWriter).write("{\"completed\" : \"10\"}");
	}

	private void verify404WasSent() throws ServletException, IOException {
		statusHandler.doGet(request, response);
		verify(response).sendError(404);
	}

}
