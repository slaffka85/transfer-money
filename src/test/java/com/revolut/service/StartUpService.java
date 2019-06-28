package com.revolut.service;

import com.revolut.controller.AccountController;
import com.revolut.controller.TransferMoneyController;
import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.model.Account;
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

import java.math.BigDecimal;
import java.util.Random;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public abstract class StartUpService {
    private static final int DEFAULT_PORT = 8081;
    private static final String SERVER_PORT_KEY = "server.port";
    private static final String CONTEXT_PATH = "/";
    private static final int JETTY_PORT = PropertyUtil.getProperty(SERVER_PORT_KEY, DEFAULT_PORT);
    private static final String JERSEY_CONFIG_SERVER_PROVIDER_CLASSNAMES = "jersey.config.server.provider.classnames";
    private static final String HTTP = "http";
    private static final String HOST = "localhost";

    protected static HttpClient client = null;
    protected static URIBuilder builder = new URIBuilder();
    protected static Logger logger = LogManager.getLogger(StartUpService.class.getSimpleName());
    protected static AccountDao accountDAO = DaoFactory.getInstance().getAccountDao();
    protected static final int COUNT_OF_ACCOUNTS = 10;
    protected static Random random = new Random();
    private static Server server;


    @BeforeClass
    public static void setup() throws Exception {
        initDB();
        startJetty();
        initClient();
    }

    private static void initDB() {
        DaoFactory.getInstance().initDb();
        createAccounts();
    }

    private static void createAccounts() {
        accountDAO = DaoFactory.getInstance().getAccountDao();
        logger.debug(String.format("creation accounts with number 1..%d", COUNT_OF_ACCOUNTS));

        for (int i = 1; i <= COUNT_OF_ACCOUNTS; i++) {
            BigDecimal balance = new BigDecimal(20000 + random.nextInt(50000));
            String username = String.format("test%d", i);
            Account account = new Account(i, username, balance);
            accountDAO.save(account);
        }
        logger.debug(String.format("creation accounts with number 1..%d completed", COUNT_OF_ACCOUNTS));
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
                    TransferMoneyController.class.getCanonicalName() + "," +
                    ControllerExceptionMapper.class.getCanonicalName();
            servletHolder.setInitParameter(JERSEY_CONFIG_SERVER_PROVIDER_CLASSNAMES, controllers);
            server.start();
        }
    }
}
