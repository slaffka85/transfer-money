package com.revolut.dao;

import com.revolut.model.Account;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;



public class TestAccountDao {

    private static AccountDao accountDAO;

    @BeforeClass
    public static void setup() {
        DaoFactory.getInstance().initDb();
        accountDAO = DaoFactory.getInstance().getAccountDao();
        accountDAO.save(new Account(1, "tssv85", new BigDecimal(35)));
    }

    @Test
    public void testFindAll() {
        List<Account> accountList = accountDAO.findAll();
        Assert.assertTrue(accountList.size() > 0);
    }

    @Test
    public void testSave() {
        Assert.assertFalse(accountDAO.findByNumber(3).isPresent());
        Account account = new Account(3, "test", new BigDecimal(50));
        accountDAO.save(account);
        Assert.assertTrue(accountDAO.findByNumber(3).isPresent());
    }

    @Test
    public void testFindByNumber() {
        Assert.assertTrue(accountDAO.findByNumber(1).isPresent());
    }

    @Test
    public void testNonExistFindByNumber() {
        Assert.assertFalse(accountDAO.findByNumber(100).isPresent());
    }

    @Test
    public void testFindByUsername() {
        Assert.assertTrue(accountDAO.findByUsername("tssv85").size() > 0);
    }

    @Test
    public void testNonExistsAccountFindByUsername() {
        Assert.assertEquals(0, accountDAO.findByUsername("tssv").size());
    }

    @Test
    public void testUpdateAll() {
        Account account4 = new Account(4, "test4", new BigDecimal(350));
        Account account5 = new Account(5, "test5", new BigDecimal(220));

        accountDAO.update(account4, account5);
    }
}
