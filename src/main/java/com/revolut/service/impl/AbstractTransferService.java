package com.revolut.service.impl;

import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.exception.NotEnoughMoneyException;
import com.revolut.model.Account;
import com.revolut.model.TransactionHistory;
import com.revolut.service.TransferService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;

public abstract class AbstractTransferService implements TransferService {

    private static final String TRANSFER_MONEY_SUCCESS_MESSAGE = "amount of value %s has been transferred from account number %s to account number %s";
    private static final String TRANSFER_MONEY_WITHIN_ONE_ACC_MESSAGE = "it's impossible to transfer money within one account number %d";
    private static final String AMOUNT_MUST_BE_POSITIVE_VALUE_MESSAGE = "amount value must be positive value. Actual is %s";

    protected Logger logger = LogManager.getLogger(this.getClass().getName());

    protected AccountDao accountDao = DaoFactory.getInstance().getAccountDao();

    private void saveTransaction(BigDecimal amount, Account from, Account to, BigDecimal balanceBeforeFrom, BigDecimal balanceBeforeTo) {
        TransactionHistory transaction = new TransactionHistory(
                from.getNumber(),
                balanceBeforeFrom,
                from.getBalance(),
                to.getNumber(),
                balanceBeforeTo,
                to.getBalance(),
                amount,
                new Date()
        );
        DaoFactory.getInstance().getTransactionHistoryDao().save(transaction);
    }

    protected void transferMoney(Account from, Account to, BigDecimal amount) {
        check(from.getNumber(), to.getNumber(), amount);
        BigDecimal balanceBeforeFrom = from.getBalance();
        BigDecimal balanceBeforeTo = to.getBalance();
        checkBalance(from, amount);
        from.withdraw(amount);
        to.deposit(amount);
        accountDao.update(from, to);
        saveTransaction(amount, from, to, balanceBeforeFrom, balanceBeforeTo);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(TRANSFER_MONEY_SUCCESS_MESSAGE, amount, from.getNumber(), to.getNumber()));
        }
    }

    private void check(long accNumberFrom, long accNumberTo, BigDecimal amount) {
        if (accNumberFrom == accNumberTo) {
            throw new IllegalArgumentException(String.format(TRANSFER_MONEY_WITHIN_ONE_ACC_MESSAGE, accNumberFrom));
        }
        if (amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException(String.format(AMOUNT_MUST_BE_POSITIVE_VALUE_MESSAGE, amount));
        }
    }

    private void checkBalance(Account accNumberFrom, BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(accNumberFrom.getBalance()) > 0 || amount.compareTo(accNumberFrom.getBalance()) > 0) {
            throw new NotEnoughMoneyException(accNumberFrom.getNumber());
        }
    }
}
