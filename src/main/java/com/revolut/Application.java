package com.revolut;

import com.revolut.controller.AccountController;
import com.revolut.controller.TransferMoneyLockController;
import com.revolut.controller.TransferMoneySyncController;
import com.revolut.dao.DaoFactory;
import com.revolut.service.JdbcExceptionMapper;
import com.revolut.service.RuntimeExceptionMapper;
import com.revolut.util.PropertyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;


public class Application {

    private static Logger log = LogManager.getLogger(Application.class);
    private static final int DEFAULT_SERVER_PORT = 8080;
    private static final String SERVER_PORT_PROPERTY = "server.port";
    private static final String JERSEY_CONFIG_SERVER_PROVIDER_CLASSNAMES = "jersey.config.server.provider.classnames";

    private static final String CONTEXT_PATH = "/";

    public static void main(String[] args)  {
        DaoFactory.getInstance().initDb();
        startJetty();
    }

    public static void startJetty() {
        int port = PropertyUtil.getProperty(SERVER_PORT_PROPERTY, DEFAULT_SERVER_PORT);
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(NO_SESSIONS);
        context.setContextPath(CONTEXT_PATH);
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/api/*");
        String controllers = AccountController.class.getCanonicalName() + "," +
                TransferMoneySyncController.class.getCanonicalName() + "," +
                TransferMoneyLockController.class.getCanonicalName() + "," +
                JdbcExceptionMapper.class.getCanonicalName() + "," +
                RuntimeExceptionMapper.class.getCanonicalName();
        servletHolder.setInitParameter(JERSEY_CONFIG_SERVER_PROVIDER_CLASSNAMES, controllers) ;
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            log.debug("it's impossible to start jetty. ", e);
        } finally {
            server.destroy();
        }
    }


}
