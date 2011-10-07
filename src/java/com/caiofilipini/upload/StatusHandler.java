package com.caiofilipini.upload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="StatusHandler", urlPatterns="/status")
public class StatusHandler extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String uid = request.getParameter("uid");
		
		if (uid != null && !uid.isEmpty()) {
			UploadProgress progress = InProgress.now(uid);
			
			if (progress == null) {
				respond404(response);
				return;
			}

			Integer percentage = progress.calculatePercentage();
			response.setHeader("Content-Type", "application/json");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write("{\"completed\" : \"" + percentage + "\"}");
		} else {
			respond404(response);
		}
	}

	private void respond404(HttpServletResponse response) throws IOException {
		response.sendError(404);
	}

}
