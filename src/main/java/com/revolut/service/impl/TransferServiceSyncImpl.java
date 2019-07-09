package com.revolut.service.impl;

import com.revolut.exception.AccountNotFoundException;
import com.revolut.model.Account;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * service for transferring money
 */
public class TransferServiceSyncImpl extends AbstractTransferService {

    private static ConcurrentMap<Long, Object> accountLockMap = new ConcurrentHashMap<>();

    /**
     * Method transfers amount of money from one account number to another.
     * @param accNumberFrom sender account number
     * @param accNumberTo receiver account number
     * @param amount amount of money
     */
    public void transferMoney(long accNumberFrom, long accNumberTo, BigDecimal amount) {
        accountLockMap.putIfAbsent(accNumberFrom, new Object());
        accountLockMap.putIfAbsent(accNumberTo, new Object());

        Object firstLock = accNumberFrom > accNumberTo ? accountLockMap.get(accNumberFrom) : accountLockMap.get(accNumberTo);
        Object secondLock = accNumberFrom > accNumberTo ? accountLockMap.get(accNumberTo) : accountLockMap.get(accNumberFrom);

        synchronized (firstLock) {
            synchronized (secondLock) {
                Account from = accountDao.findByNumber(accNumberFrom).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));
                Account to = accountDao.findByNumber(accNumberTo).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));
                super.transferMoney(from, to, amount);
            }
        }
    }
}
