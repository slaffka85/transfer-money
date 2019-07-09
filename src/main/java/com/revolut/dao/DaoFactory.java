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
 * Data access object factory
 *
 * @author v.tsapaev
 */
public abstract class DaoFactory {
    private static DaoFactory instance;

    /**
     *
     */
    DaoFactory() {}

    /**
     * Instance getter.
     *
     * @return DAOFactory
     */
    public static DaoFactory getInstance() {
        if (instance == null) {
            synchronized (DaoFactory.class) {
                if (instance == null) {
                    instance = new JdbcH2DaoFactory();
                }
            }
        }
        return instance;
    }

    /**
     * Method throws exception.
     *
     * @return Nothing
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * method for initialisation db
     */
    public abstract void initDb();

    /**
     * getter for {@link AccountDao}
     * @return {@link AccountDao}
     */
    public abstract AccountDao getAccountDao();

    /**
     * getter for {@link TransactionHistoryDao}
     * @return {@link TransactionHistoryDao}
     */
    public abstract TransactionHistoryDao getTransactionHistoryDao();

    /**
     * jdbc h2 data access object factory
     */
    private static class JdbcH2DaoFactory extends DaoFactory {

        private static final Logger logger = LogManager.getLogger(JdbcH2DaoFactory.class.getName());
        private static final String URL = PropertyUtil.getProperty("datasource.url","jdbc:h2:mem:./moneytransfer;DB_CLOSE_DELAY=-1");
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
}
