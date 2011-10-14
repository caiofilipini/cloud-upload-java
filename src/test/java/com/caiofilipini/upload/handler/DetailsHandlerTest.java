package com.caiofilipini.upload.handler;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.caiofilipini.upload.handler.DetailsHandler;
import com.caiofilipini.upload.progress.InProgress;
import com.caiofilipini.upload.progress.UploadProgress;

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
        setupPostRequest("29", "Everyone has sounds to share.");

        detailsHandler.doPost(request, response);

        verify(request).setAttribute("filePath", "/files/29.mp3");
        verify(request).setAttribute("details", "Everyone has sounds to share.");
    }

    @Test
    public void shouldRedirectToDetailsPage() throws Exception {
        setupPostRequest("57", "Everyone has sounds to share.");

        detailsHandler.doPost(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/jsp/details.jsp");
    }

    private void setupPostRequest(String uid, String details) {
        when(request.getParameter("uid")).thenReturn(uid);
        when(request.getParameter("details")).thenReturn(details);

        UploadProgress progress = new UploadProgress(42L, 42L);
        progress.fileAvailableAt("/files/" + uid + ".mp3");
        InProgress.store(uid, progress);
    }

}
