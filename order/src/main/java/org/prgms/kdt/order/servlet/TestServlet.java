package org.prgms.kdt.order.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebServlet(value = "/*", loadOnStartup = 1)
public class TestServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("Init Servlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var requestURI = req.getRequestURI();
        logger.info("Get request from {}", requestURI);

        var writer = resp.getWriter();
        writer.println("Hello Servlet!");
    }
}
