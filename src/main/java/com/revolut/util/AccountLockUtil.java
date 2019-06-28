package com.revolut.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccountLockUtil {

    private final static ConcurrentMap<Long, Object> accountNumberLockMap = new ConcurrentHashMap<>();

    private AccountLockUtil() {
        //preventing instance creation
    }

    public static Object getLockForAccount(Long number) {
        accountNumberLockMap.putIfAbsent(number, new Object());
        return accountNumberLockMap.get(number);
    }
}
