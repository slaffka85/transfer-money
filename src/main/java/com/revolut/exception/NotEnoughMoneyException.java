package com.revolut.exception;

public class NotEnoughMoneyException extends RuntimeException {

    private static final String MESSAGE = "account with number %d hasn't enough money";

    public NotEnoughMoneyException(long accNumber) {
        super(String.format(MESSAGE, accNumber));
    }

}
