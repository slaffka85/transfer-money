package com.revolut.dao;

import com.revolut.model.Account;
import com.revolut.model.TransactionHistory;
import com.revolut.service.TransferService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class TestTransactionHistoryDao {

    private static TransferService transferService;
    private static AccountDao accountDAO;
    private static TransactionHistoryDao transactionHistoryDao;

    @BeforeClass
    public static void setup() {
        DaoFactory.getInstance().initDb();
        transferService = new TransferService();
        accountDAO = DaoFactory.getInstance().getAccountDao();
        transactionHistoryDao = DaoFactory.getInstance().getTransactionHistoryDao();

        Account account1 = new Account(1, "test1", new BigDecimal(100));
        Account account2 = new Account(2, "test2", new BigDecimal(500));
        accountDAO.save(account1);
        accountDAO.save(account2);
    }

    @Test
    public void testFindAllTransactionHistory() {
        List<TransactionHistory> transactionHistoryList = transactionHistoryDao.findAll();
        Assert.assertEquals(0, transactionHistoryList.size());
        transferService.transferMoney(1, 2, new BigDecimal(3));

        transactionHistoryList = transactionHistoryDao.findAll();
        Assert.assertEquals(1, transactionHistoryList.size());
        TransactionHistory transactionHistory = transactionHistoryList.get(0);
        System.out.println(transactionHistory);
        Assert.assertEquals(1, transactionHistory.getAccNumberFrom());
        Assert.assertEquals(0, new BigDecimal(100).compareTo(transactionHistory.getBalanceBeforeFrom()));
        Assert.assertEquals(0, new BigDecimal(97).compareTo(transactionHistory.getBalanceAfterFrom()));
        Assert.assertEquals(2, transactionHistory.getAccNumberTo());
        Assert.assertEquals(0, new BigDecimal(500).compareTo(transactionHistory.getBalanceBeforeTo()));
        Assert.assertEquals(0, new BigDecimal(503).compareTo(transactionHistory.getBalanceAfterTo()));
        Assert.assertEquals(0, new BigDecimal(3).compareTo(transactionHistory.getAmount()));


    }
}
