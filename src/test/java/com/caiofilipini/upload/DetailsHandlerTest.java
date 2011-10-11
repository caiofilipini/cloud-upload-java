package com.caiofilipini.upload;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class DetailsHandlerTest {

	private HttpServletRequest request;
    private HttpServletResponse response;
	private DetailsHandler detailsHandler;

    @Before
    public void createSubject() {
        detailsHandler = new DetailsHandler();
    }

    @Before
    public void createRequestAndResponse() throws Exception {
    	RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        response = mock(HttpServletResponse.class);
        request = mock(HttpServletRequest.class);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void shouldMakeFilePathAndDetailsAvailableForRendering() throws Exception {
    	String uid = "29";
    	UploadProgress progress = new UploadProgress(29L, 29L);
    	progress.complete("/files/29.mp3");
    	InProgress.store(uid, progress);

    	when(request.getParameter("uid")).thenReturn(uid);
    	when(request.getParameter("details")).thenReturn("Everyone has sounds to share.");

    	detailsHandler.doPost(request, response);

    	verify(request).setAttribute("filePath", "/files/29.mp3");
    	verify(request).setAttribute("details", "Everyone has sounds to share.");
    }

    @Test
    public void shouldRedirectToDetailsPage() throws Exception {
    	String uid = "57";
    	UploadProgress progress = new UploadProgress(57L, 57L);
    	progress.complete("/files/57.mp3");
    	InProgress.store(uid, progress);

    	when(request.getParameter("uid")).thenReturn(uid);
    	when(request.getParameter("details")).thenReturn("Everyone has sounds to share.");

    	detailsHandler.doPost(request, response);

    	verify(request).getRequestDispatcher("/WEB-INF/jsp/details.jsp");
    }

}
