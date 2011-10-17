package com.caiofilipini.upload.handler;

import java.io.IOException;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormHandler extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long uid = System.currentTimeMillis();

        request.setAttribute("uid", uid);
        request.getRequestDispatcher("WEB-INF/jsp/uploadForm.jsp").forward(request, response);
    }

}
