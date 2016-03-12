package edu.nju.paperCiteAnalysis.recommendation;

import edu.nju.paperCiteAnalysis.recommendation.front.RecommendFrontService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Created by hazel on 2016-03-11.
 * Recommendation Main
 */
public class Recommendation {

    public static void main(String[] args) {
        initJettyServer();
    }

    private static void initJettyServer() {
        Server jettyServer = new Server(8080);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setResourceBase(Recommendation.class.getResource("/webapp").getPath());
        webAppContext.setContextPath("/client");

        ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                RecommendFrontService.class.getCanonicalName());
        ServletContextHandler servletContextHandler = new ServletContextHandler(jettyServer, "/api/", ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(servletHolder, "/*");

        HandlerCollection collection = new HandlerCollection();
        collection.setHandlers(new Handler[]{webAppContext, servletContextHandler});

        jettyServer.setHandler(collection);

        try {
            jettyServer.start();
            jettyServer.join();
            System.out.println("Jetty server started.");
        } catch (Exception e) {
            System.err.println("Errors occurred when start jetty server:  " + e.getMessage());
            System.exit(1);
        } finally {
            jettyServer.destroy();
        }
    }
}
