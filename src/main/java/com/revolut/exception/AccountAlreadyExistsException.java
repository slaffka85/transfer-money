package com.revolut.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    private static final String ACCOUNT_ALREADY_EXISTS_MESSAGE = "account with number %d already exists";

    public AccountAlreadyExistsException(long accNumber) {
        super(String.format(ACCOUNT_ALREADY_EXISTS_MESSAGE, accNumber));
    }
}
