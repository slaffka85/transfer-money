package com.revolut.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revolut.util.AccountLockUtil;

import java.math.BigDecimal;

public final class Account {

    @JsonIgnore
    private Object lock;

    private long number;

    private String username;

    private BigDecimal balance;

    public Account(long number, String username, BigDecimal balance) {
        this.number = number;
        this.username = username;
        this.balance = balance;
        lock = AccountLockUtil.getLockForAccount(number);
    }

    public Long getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Object getLock() {
        return lock;
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "number=" + number +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }
}
