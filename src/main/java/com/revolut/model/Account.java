package com.revolut.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revolut.util.AccountLockUtil;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

/**
 * model of user account
 */
public final class Account {

    @JsonIgnore
    private Lock lock;

    private long number;

    private String username;

    private BigDecimal balance;

    public Account() {

    }

    public Account(long number, String username, BigDecimal balance) {
        this.number = number;
        this.username = username;
        this.balance = balance;
        lock = AccountLockUtil.getLockForAccount(number);
    }

    public void setNumber(long number) {
        if (lock == null) {
            lock = AccountLockUtil.getLockForAccount(number);
        }
        this.number = number;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public long getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Lock getLock() {
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
