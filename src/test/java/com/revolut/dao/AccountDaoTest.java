package com.revolut.dao;

import com.revolut.exception.JdbcException;
import com.revolut.helper.AssertUtil;
import com.revolut.model.Account;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public class AccountDaoTest {

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
    public void testSuccessfulSave() {
        Assert.assertFalse(accountDAO.findByNumber(3).isPresent());
        Account newAccount = new Account(3, "test", new BigDecimal(50));
        accountDAO.save(newAccount);
        Optional<Account> optionalAccount = accountDAO.findByNumber(3);
        Assert.assertTrue(optionalAccount.isPresent());
        Account account = optionalAccount.get();
        AssertUtil.assertEquals(new BigDecimal(50), account.getBalance());
        Assert.assertEquals("test", account.getUsername());
        Assert.assertEquals(3, account.getNumber());
    }

    @Test(expected = JdbcException.class)
    public void testSaveExistedAccount() {
        Assert.assertTrue(accountDAO.findByNumber(1).isPresent());
        Account newAccount = new Account(1, "test", new BigDecimal(50));
        Account account = accountDAO.save(newAccount);
        Assert.assertNull(account);
    }

    @Test
    public void testFindByNumber() {
        Optional<Account> optionalAccount = accountDAO.findByNumber(1);
        Assert.assertTrue(optionalAccount.isPresent());
        Account account = optionalAccount.get();
        AssertUtil.assertEquals(new BigDecimal(35), account.getBalance());
        Assert.assertEquals("tssv85", account.getUsername());
        Assert.assertEquals(1, account.getNumber());
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
    public void testNonExistFindByUsername() {
        Assert.assertTrue(accountDAO.findByUsername("miketyson").size() == 0);
    }


    @Test
    public void testUpdateAll() {
        Account account4 = new Account(4, "test4", new BigDecimal(350));
        Account account5 = new Account(5, "test5", new BigDecimal(220));

        accountDAO.save(account4);
        accountDAO.save(account5);

        account4.setBalance(new BigDecimal(300));
        account5.setBalance(new BigDecimal(200));

        accountDAO.update(account4, account5);

        account4 = accountDAO.findByNumber(4).get();
        account5 = accountDAO.findByNumber(5).get();
        AssertUtil.assertEquals(new BigDecimal(300), account4.getBalance());
        Assert.assertEquals("test4", account4.getUsername());
        AssertUtil.assertEquals(new BigDecimal(200), account5.getBalance());
        Assert.assertEquals("test5", account5.getUsername());
    }
}
