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
        try {
            String uid = request.getParameter("uid");
            respond200WithJson(response, uid);
        } catch (ProgressNotFoundException e) {
            respond404(response);
        }
    }

    private void respond200WithJson(HttpServletResponse response, String uid) throws IOException {
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(progressAsJson(uid));
    }

    private String progressAsJson(String uid) {
        UploadProgress progress = InProgress.now(uid);
        return "{\"completed\" : \"" + progress.calculatePercentage() + "\"}";
    }

    private void respond404(HttpServletResponse response) throws IOException {
        response.sendError(404);
    }

}
