package com.revolut.exception;

import java.sql.SQLException;

public class JdbcException extends RuntimeException {

    public JdbcException(String message, SQLException e) {
        super(message, e);
    }
}
