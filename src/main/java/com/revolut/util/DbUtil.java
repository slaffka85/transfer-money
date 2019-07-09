package com.revolut.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Util helps working with DB
 */
public class DbUtil {

    private static Logger logger = LogManager.getLogger(DbUtil.class.getName());

    private DbUtil() {
        //preventing instance creation
    }

    /**
     * method sets auto commit mode true for connection and close connection quietly
     * @param connection is DB connection
     */
    public static void setAutoCommitTrueAndCloseQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                logger.error("it's impossible to set auto commit true and close connection", e);
            }
        }
    }

    /**
     * method rollbacks transaction for current connection quietly
     * @param connection is current connection
     */
    public static void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("it's impossible to rollback transaction", ex);
            }
        }
    }
}
