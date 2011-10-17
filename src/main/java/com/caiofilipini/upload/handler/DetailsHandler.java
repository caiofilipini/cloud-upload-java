package com.caiofilipini.upload.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caiofilipini.upload.progress.InProgress;

public class DetailsHandler extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uid = request.getParameter("uid");
        String fileDetails = request.getParameter("details");

        request.setAttribute("filePath", InProgress.now(uid).getFilePath());
        request.setAttribute("details", fileDetails);

        request.getRequestDispatcher("/WEB-INF/jsp/details.jsp")
                .forward(request, response);
    }

}
