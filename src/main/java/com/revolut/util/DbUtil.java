package com.revolut.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Util helps working with DB
 */
public class DbUtil {

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
                e.printStackTrace();
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
                ex.printStackTrace();
            }
        }
    }
}
