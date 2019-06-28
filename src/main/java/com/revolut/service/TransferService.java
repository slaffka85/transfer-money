package com.revolut.service;

import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.NotEnoughMoneyException;
import com.revolut.model.Account;
import com.revolut.model.TransactionHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * service for transferring money
 */
public class TransferService {

    private static Logger log = LogManager.getLogger(TransferService.class);
    private static ConcurrentMap<Long, Object> accountLockMap = new ConcurrentHashMap<>();


    /**
     * Method transfers amount of money from one account number to another.
     * @param accNumberFrom sender account number
     * @param accNumberTo receiver account number
     * @param amount amount of money
     */
    public void transferMoney(long accNumberFrom, long accNumberTo, BigDecimal amount) {
        if (accNumberFrom == accNumberTo) {
            throw new IllegalArgumentException(String.format("it's impossible to transfer money within one account number %d", accNumberFrom));
        }
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(String.format("amount value must be positive value. Actual is %s", amount));
        }
        accountLockMap.putIfAbsent(accNumberFrom, new Object());
        accountLockMap.putIfAbsent(accNumberTo, new Object());

        Object firstLock = accNumberFrom > accNumberTo ? accountLockMap.get(accNumberFrom) : accountLockMap.get(accNumberTo);
        Object secondLock = accNumberFrom > accNumberTo ? accountLockMap.get(accNumberTo) : accountLockMap.get(accNumberFrom);

        synchronized (firstLock) {
            synchronized (secondLock) {
                AccountDao accountDao = DaoFactory.getInstance().getAccountDao();
                Account from = accountDao.findByNumber(accNumberFrom).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));
                Account to = accountDao.findByNumber(accNumberTo).orElseThrow(() -> new AccountNotFoundException(accNumberFrom));

                BigDecimal balanceBeforeFrom = from.getBalance();
                BigDecimal balanceBeforeTo = to.getBalance();
                if (BigDecimal.ZERO.compareTo(balanceBeforeFrom) > 0 || amount.compareTo(balanceBeforeFrom) > 0) {
                    throw new NotEnoughMoneyException(String.format("account with number %d hasn't enough money", accNumberFrom));
                }
                from.withdraw(amount);
                to.deposit(amount);
                accountDao.update(to, from);
                TransactionHistory transaction = new TransactionHistory(
                        accNumberFrom,
                        balanceBeforeFrom,
                        from.getBalance(),
                        accNumberTo,
                        balanceBeforeTo,
                        to.getBalance(),
                        amount,
                        LocalDateTime.now()
                );
                DaoFactory.getInstance().getTransactionHistoryDao().save(transaction);
                log.debug(String.format("amount of value %s has been transferred from account number %s to account number %s", amount, accNumberFrom, accNumberTo));
            }
        }
    }
}
