package com.revolut.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountLockUtil {

    private static final ConcurrentMap<Long, Lock> accountNumberLockMap = new ConcurrentHashMap<>();

    private AccountLockUtil() {
        //preventing instance creation
    }

    public static Lock getLockForAccount(Long number) {
        accountNumberLockMap.putIfAbsent(number, new ReentrantLock());
        return accountNumberLockMap.get(number);
    }
}
