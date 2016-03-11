package edu.nju.paperCiteAnalysis.recommendation;

import edu.nju.paperCiteAnalysis.recommendation.view.HelloJetty;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Created by hazel on 2016-03-11.
 */
public class Main {

    public static void main(String[] args) {
        initJettyServer();
    }

    private static void initJettyServer() {
        ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                HelloJetty.class.getCanonicalName());

        Server jettyServer = new Server(8080);
        ServletContextHandler context =
                new ServletContextHandler(jettyServer, "/", ServletContextHandler.SESSIONS);
        context.addServlet(servletHolder, "/*");

        try {
            jettyServer.start();
            jettyServer.join();
            System.out.println("Jetty server started.");
        } catch (Exception e) {
            System.out.println("Errors occurred when start jetty server:  " + e.getMessage());
            System.exit(1);
        } finally {
            jettyServer.destroy();
        }
    }
}
