package edu.nju.paperCiteAnalysis.recommendation;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

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
        WebAppContext ctx = new WebAppContext();
        ctx.setDescriptor(Recommendation.class.getResource("/webapp/WEB-INF/web.xml").getPath());
        ctx.setResourceBase(Recommendation.class.getResource("/webapp").getPath());
        ctx.setContextPath("/");

        //Including the JSTL jars for the webapp.
        ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*jstl.*\\.jar$");

        //Enabling the Annotation based configuration
        Configuration.ClassList classList =
                Configuration.ClassList.setServerDefault(jettyServer);
        classList.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classList.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");

        jettyServer.setHandler(ctx);
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
