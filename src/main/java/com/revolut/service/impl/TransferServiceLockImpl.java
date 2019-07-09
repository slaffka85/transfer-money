package com.revolut.service.impl;

import com.revolut.exception.AccountNotFoundException;
import com.revolut.model.Account;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

public class TransferServiceLockImpl extends AbstractTransferService {

    private static final String FAIL_MESSAGE = "it's impossible to transfer amount of money %s from account number %d to account number %d";

    @Override
    public void transferMoney(long accNumberFrom, long accNumberTo, BigDecimal amount) {

        Account from = accountDao.findByNumber(accNumberFrom).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));
        Account to = accountDao.findByNumber(accNumberTo).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));

        Lock firstLock = accNumberFrom > accNumberTo ? from.getLock() : to.getLock();
        Lock secondLock = accNumberFrom > accNumberTo ? to.getLock() : from.getLock();

        firstLock.lock();
        secondLock.lock();
        try {
            from = accountDao.findByNumber(accNumberFrom).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));
            to = accountDao.findByNumber(accNumberTo).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));
            transferMoney(from, to, amount);
        } catch (RuntimeException e) {
            logger.warn(String.format(FAIL_MESSAGE, amount, accNumberFrom, accNumberTo));
            throw e;
        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
    }




}
