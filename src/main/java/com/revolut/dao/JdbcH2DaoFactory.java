package com.revolut.dao;

import com.revolut.dao.impl.JdbcH2AccountDaoImpl;
import com.revolut.dao.impl.JdbcH2TransactionHistoryDaoImpl;
import com.revolut.util.PropertyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * jdbc h2 data access object factory
 */
public class JdbcH2DaoFactory extends DaoFactory {

    private static final Logger logger = LogManager.getLogger(JdbcH2DaoFactory.class.getName());
    private static final String URL = PropertyUtil.getProperty("datasource.url","jdbc:h2:file:/C:/Users/developer/IdeaProjects/moneytransfer/test");
    private static final String USER = PropertyUtil.getProperty("datasource.user","sa");
    private static final String PASSWORD = PropertyUtil.getProperty("datasource.password", "");
    private static JdbcConnectionPool cp = JdbcConnectionPool.create(URL, USER, PASSWORD);

    private JdbcConnectionPool getJdbcConnectionPool() {
        return cp;
    }

    /**
     * getter for {@link AccountDao}
     * @return {@link AccountDao}
     */
    public AccountDao getAccountDao() {
        return new JdbcH2AccountDaoImpl(cp);
    }

    /**
     * getter for {@link TransactionHistoryDao}
     * @return {@link TransactionHistoryDao}
     */
    public TransactionHistoryDao getTransactionHistoryDao() {
        return new JdbcH2TransactionHistoryDaoImpl(cp);
    }

    /**
     * method for initialisation db
     */
    public void initDb() {
        logger.info("init db");
        try (Connection conn = getJdbcConnectionPool().getConnection()){
            RunScript.execute(conn, new FileReader(PropertyUtil.getProperty("datasource.import_file","src/main/resources/import.sql")));
            logger.info("init db completed");
        } catch (SQLException | FileNotFoundException e) {
            logger.warn("init db fails", e);
        }
    }

}
