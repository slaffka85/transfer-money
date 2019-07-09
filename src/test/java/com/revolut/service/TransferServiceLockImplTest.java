package com.revolut.service;

import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.exception.NotEnoughMoneyException;
import com.revolut.model.Account;
import com.revolut.service.impl.TransferServiceLockImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

public class TransferServiceLockImplTest {

    private static Logger logger = LogManager.getLogger(TransferServiceLockImplTest.class);
    private static TransferService transferService;
    private static AccountDao accountDAO;

    @BeforeClass
    public static void setup() {
        DaoFactory.getInstance().initDb();
        transferService = new TransferServiceLockImpl();
        accountDAO = DaoFactory.getInstance().getAccountDao();

        Account account1 = new Account(3, "test1", new BigDecimal(100));
        Account account2 = new Account(4, "test2", new BigDecimal(500));
        accountDAO.save(account1);
        accountDAO.save(account2);
    }

    @Test
    public void testTransferMoney() {
        Account from = accountDAO.findByNumber(3).get();
        Account to = accountDAO.findByNumber(4).get();

        Assert.assertEquals(0, new BigDecimal(100).compareTo(from.getBalance()));
        Assert.assertEquals(0, new BigDecimal(500).compareTo(to.getBalance()));

        transferService.transferMoney(from.getNumber(), to.getNumber(), new BigDecimal(30));

        from = accountDAO.findByNumber(3).get();
        to = accountDAO.findByNumber(4).get();

        Assert.assertEquals(0, new BigDecimal(70).compareTo(from.getBalance()));
        Assert.assertEquals(0, new BigDecimal(530).compareTo(to.getBalance()));
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void testFailTransferMoney() {
        Account from = accountDAO.findByNumber(3).get();
        Account to = accountDAO.findByNumber(4).get();

        BigDecimal balanceFromAccountStart = from.getBalance();
        BigDecimal balanceToAccountStart = to.getBalance();

        int bigAmount = 5000000;
        Assert.assertEquals(1, new BigDecimal(bigAmount).compareTo(from.getBalance()));

        transferService.transferMoney(from.getNumber(), to.getNumber(), new BigDecimal(bigAmount));

        from = accountDAO.findByNumber(3).get();
        to = accountDAO.findByNumber(4).get();
        BigDecimal balanceFromAccountEnd = from.getBalance();
        BigDecimal balanceToAccountEnd = to.getBalance();

        Assert.assertEquals(0, balanceFromAccountStart.compareTo(balanceFromAccountEnd));
        Assert.assertEquals(0, balanceToAccountStart.compareTo(balanceToAccountEnd));
    }
}
