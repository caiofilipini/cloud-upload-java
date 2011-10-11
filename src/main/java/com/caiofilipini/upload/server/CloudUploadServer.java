package com.caiofilipini.upload.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class CloudUploadServer {
    public static void main(String[] args) throws Exception {
       Server server = new Server(Integer.valueOf(System.getenv("PORT")));

       WebAppContext root = new WebAppContext();
       root.setContextPath("/");
       root.setDescriptor("src/main/webapp/WEB-INF/web.xml");
       root.setResourceBase("src/main/webapp/");
       root.setParentLoaderPriority(true);

       server.setHandler(root);
       server.start();
       server.join();
    }
}
