package com.revolut.rest;

import com.revolut.controller.AccountController;
import com.revolut.controller.TransactionHistoryController;
import com.revolut.controller.TransferMoneyLockController;
import com.revolut.controller.TransferMoneySyncController;
import com.revolut.service.JdbcExceptionMapper;
import com.revolut.service.RuntimeExceptionMapper;
import com.revolut.util.PropertyUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.BeforeClass;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public abstract class IntTest {
    private static final int DEFAULT_PORT = 8081;
    private static final String SERVER_PORT_KEY = "server.port";
    private static final String CONTEXT_PATH = "/";
    private static final int JETTY_PORT = PropertyUtil.getProperty(SERVER_PORT_KEY, DEFAULT_PORT);
    private static final String JERSEY_CONFIG_SERVER_PROVIDER_CLASSNAMES = "jersey.config.server.provider.classnames";
    private static final String HTTP = "http";
    private static final String HOST = "localhost";

    protected static HttpClient client = null;
    protected static URIBuilder builder = new URIBuilder();
    protected static Logger logger = LogManager.getLogger(IntTest.class.getSimpleName());
    private static Server server;


    @BeforeClass
    public static void setup() throws Exception {
        startJetty();
        initClient();
        //initDB();
    }



    private static void initClient() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(1000);
        connManager.setMaxTotal(2000);
        client = HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .build();
        builder = new URIBuilder();
        builder.setScheme(HTTP);
        builder.setHost(HOST);
        builder.setPort(JETTY_PORT);
    }

    private static void startJetty() throws Exception {
        if (server == null) {
            server = new Server(JETTY_PORT);
            ServletContextHandler context = new ServletContextHandler(NO_SESSIONS);
            context.setContextPath(CONTEXT_PATH);
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/api/*");
            String controllers = AccountController.class.getCanonicalName() + "," +
                    TransferMoneySyncController.class.getCanonicalName() + "," +
                    TransferMoneyLockController.class.getCanonicalName() + "," +
                    TransactionHistoryController.class.getCanonicalName() + "," +
                    JdbcExceptionMapper.class.getCanonicalName() + "," +
                    RuntimeExceptionMapper.class.getCanonicalName();
            servletHolder.setInitParameter(JERSEY_CONFIG_SERVER_PROVIDER_CLASSNAMES, controllers);
            server.start();
        }
    }
}
