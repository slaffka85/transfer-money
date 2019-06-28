package com.revolut.exception;

public class AccountNotFoundException extends RuntimeException {

    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "account with number %d not found in DB";

    public AccountNotFoundException(long accNumber) {
        super(String.format(ACCOUNT_NOT_FOUND_MESSAGE, accNumber));
    }
}
