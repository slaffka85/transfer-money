package com.revolut.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.helper.AssertUtil;
import com.revolut.model.Account;
import com.revolut.model.TransactionHistory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public abstract class AbstractTransferMoneyControllerIntTest extends IntTest {

    private ObjectMapper mapper = new ObjectMapper();

    protected static AccountDao accountDao;

    protected void initDb() {
        DaoFactory.getInstance().initDb();
        accountDao = DaoFactory.getInstance().getAccountDao();
        Account account1 = new Account(3, "test1", new BigDecimal(100));
        Account account2 = new Account(4, "test2", new BigDecimal(500));
        accountDao.save(account1);
        DaoFactory.getInstance().getAccountDao().save(account2);

        mapper.findAndRegisterModules();
    }


    protected void testPositiveTransferMoney(String path) throws Exception {
        long accFrom = 3;
        long accTo = 4;
        BigDecimal amount = new BigDecimal(30);
        Optional<Account> optionalFrom = accountDao.findByNumber(3);
        Optional<Account> optionalTo = accountDao.findByNumber(4);
        Assert.assertTrue(optionalTo.isPresent());
        Assert.assertTrue(optionalFrom.isPresent());
        Account from = optionalFrom.get();
        Account to = optionalTo.get();

        AssertUtil.assertEquals(new BigDecimal(100), from.getBalance());
        AssertUtil.assertEquals(new BigDecimal(500), to.getBalance());

        this.testIfTransactionHistoryEmpty();

        String fullPath = path + accFrom + "/" + accTo + "/" + amount;
        URI uri = builder.setPath(fullPath).build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);

        Optional<Account> optionalAfterFrom = accountDao.findByNumber(3);
        Optional<Account> optionalAfterTo = accountDao.findByNumber(4);
        Assert.assertTrue(optionalAfterTo.isPresent());
        Assert.assertTrue(optionalAfterFrom.isPresent());
        Account fromAfter = optionalAfterFrom.get();
        Account toAfter = optionalAfterTo.get();

        AssertUtil.assertEquals(new BigDecimal(70), fromAfter.getBalance());
        AssertUtil.assertEquals(new BigDecimal(530), toAfter.getBalance());
        Assert.assertEquals(from.getNumber(), fromAfter.getNumber());
        Assert.assertEquals(to.getNumber(), to.getNumber());
        Assert.assertEquals(from.getUsername(), fromAfter.getUsername());

        uri = builder.setPath("/api/transaction-history").build();
        HttpGet thRequest = new HttpGet(uri);
        HttpResponse thResponse = client.execute(thRequest);
        statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
        String jsonString = EntityUtils.toString(thResponse.getEntity());
        TransactionHistory[] responseTransactionHistories = mapper.readValue(jsonString, TransactionHistory[].class);
        Assert.assertEquals(1, responseTransactionHistories.length);
        TransactionHistory transactionHistory = responseTransactionHistories[0];
        Assert.assertEquals(accFrom, transactionHistory.getAccNumberFrom());
        Assert.assertEquals(accTo, transactionHistory.getAccNumberTo());
        AssertUtil.assertEquals(amount, transactionHistory.getAmount());
        AssertUtil.assertEquals(new BigDecimal(100), transactionHistory.getBalanceBeforeFrom());
        AssertUtil.assertEquals(new BigDecimal(500), transactionHistory.getBalanceBeforeTo());
        AssertUtil.assertEquals(new BigDecimal(70), transactionHistory.getBalanceAfterFrom());
        AssertUtil.assertEquals(new BigDecimal(530), transactionHistory.getBalanceAfterTo());
    }


    public void testNotEnoughMoneyTransferMoney(String path) throws Exception {
        long accFrom = 3;
        long accTo = 4;
        BigDecimal amount = new BigDecimal(1000000);
        Optional<Account> optionalFrom = accountDao.findByNumber(accFrom);
        Optional<Account> optionalTo = accountDao.findByNumber(accTo);
        Assert.assertTrue(optionalTo.isPresent());
        Assert.assertTrue(optionalFrom.isPresent());
        Account from = optionalFrom.get();
        Account to = optionalTo.get();
        AssertUtil.assertEquals(new BigDecimal(100), from.getBalance());
        AssertUtil.assertEquals(new BigDecimal(500), to.getBalance());

        this.testIfTransactionHistoryEmpty();

        String fullPath = path + accFrom + "/" + accTo + "/" + amount;
        URI uri = builder.setPath(fullPath).build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        String jsonString = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("account with number 3 hasn't enough money", jsonString);

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
        Optional<Account> optionalAfterFrom = accountDao.findByNumber(3);
        Optional<Account> optionalAfterTo = accountDao.findByNumber(4);
        Assert.assertTrue(optionalAfterTo.isPresent());
        Assert.assertTrue(optionalAfterFrom.isPresent());
        Account fromAfter = optionalAfterFrom.get();
        Account toAfter = optionalAfterTo.get();
        AssertUtil.assertEquals(new BigDecimal(100), fromAfter.getBalance());
        AssertUtil.assertEquals(new BigDecimal(500), toAfter.getBalance());
        Assert.assertEquals(from.getNumber(), fromAfter.getNumber());
        Assert.assertEquals(to.getNumber(), to.getNumber());
        Assert.assertEquals(from.getUsername(), fromAfter.getUsername());
        this.testIfTransactionHistoryEmpty();
    }

    public void testTransferMoneyWithinOneAccount(String path) throws Exception {
        long accFrom = 3;
        long accTo = accFrom;
        BigDecimal amount = new BigDecimal(1000000);
        Optional<Account> optionalFrom = accountDao.findByNumber(accFrom);
        Assert.assertTrue(optionalFrom.isPresent());
        Account from = optionalFrom.get();
        AssertUtil.assertEquals(new BigDecimal(100), from.getBalance());
        String fullPath = path + accFrom + "/" + accTo + "/" + amount;

        URI uri = builder.setPath(fullPath).build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        System.out.println(jsonString);
        Assert.assertEquals("it's impossible to transfer money within one account number 3", jsonString);

        Optional<Account> optionalAfterFrom = accountDao.findByNumber(accFrom);
        Assert.assertTrue(optionalAfterFrom.isPresent());
        Account fromAfter = optionalAfterFrom.get();

        AssertUtil.assertEquals(new BigDecimal(100), fromAfter.getBalance());
        Assert.assertEquals(from.getNumber(), fromAfter.getNumber());
        Assert.assertEquals(from.getUsername(), fromAfter.getUsername());
        this.testIfTransactionHistoryEmpty();
    }

    public void testTransferNegativeAmountOfMoneyAccount(String path) throws Exception {
        long accFrom = 3;
        long accTo = 4;
        BigDecimal amount = new BigDecimal(-20);
        Optional<Account> optionalFrom = accountDao.findByNumber(accFrom);
        Optional<Account> optionalTo = accountDao.findByNumber(accTo);
        Assert.assertTrue(optionalTo.isPresent());
        Assert.assertTrue(optionalFrom.isPresent());
        Account from = optionalFrom.get();
        Account to = optionalTo.get();
        AssertUtil.assertEquals(new BigDecimal(100), from.getBalance());
        AssertUtil.assertEquals(new BigDecimal(500), to.getBalance());

        String fullPath = path + accFrom + "/" + accTo + "/" + amount;

        URI uri = builder.setPath(fullPath).build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        System.out.println(jsonString);
        Assert.assertEquals("amount value must be positive value. Actual is -20", jsonString);

        Optional<Account> optionalAfterFrom = accountDao.findByNumber(accFrom);
        Optional<Account> optionalAfterTo = accountDao.findByNumber(accTo);
        Assert.assertTrue(optionalAfterTo.isPresent());
        Assert.assertTrue(optionalAfterFrom.isPresent());
        Account fromAfter = optionalAfterFrom.get();
        Account toAfter = optionalAfterTo.get();

        AssertUtil.assertEquals(new BigDecimal(100), fromAfter.getBalance());
        AssertUtil.assertEquals(new BigDecimal(500), toAfter.getBalance());
        Assert.assertEquals(from.getNumber(), fromAfter.getNumber());
        Assert.assertEquals(to.getNumber(), to.getNumber());
        Assert.assertEquals(from.getUsername(), fromAfter.getUsername());
        this.testIfTransactionHistoryEmpty();
    }

    public void testTransferZeroAmountOfMoney(String path) throws Exception {
        long accFrom = 3;
        long accTo = 4;
        BigDecimal amount = BigDecimal.ZERO;
        Optional<Account> optionalFrom = accountDao.findByNumber(accFrom);
        Optional<Account> optionalTo = accountDao.findByNumber(accTo);
        Assert.assertTrue(optionalTo.isPresent());
        Assert.assertTrue(optionalFrom.isPresent());
        Account from = optionalFrom.get();
        Account to = optionalTo.get();
        AssertUtil.assertEquals(new BigDecimal(100), from.getBalance());
        AssertUtil.assertEquals(new BigDecimal(500), to.getBalance());

        String fullPath = path + accFrom + "/" + accTo + "/" + amount;

        URI uri = builder.setPath(fullPath).build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        System.out.println(jsonString);
        Assert.assertEquals("amount value must be positive value. Actual is 0", jsonString);

        Optional<Account> optionalAfterFrom = accountDao.findByNumber(accFrom);
        Optional<Account> optionalAfterTo = accountDao.findByNumber(accTo);
        Assert.assertTrue(optionalAfterTo.isPresent());
        Assert.assertTrue(optionalAfterFrom.isPresent());
        Account fromAfter = optionalAfterFrom.get();
        Account toAfter = optionalAfterTo.get();

        AssertUtil.assertEquals(new BigDecimal(100), fromAfter.getBalance());
        AssertUtil.assertEquals(new BigDecimal(500), toAfter.getBalance());
        Assert.assertEquals(from.getNumber(), fromAfter.getNumber());
        Assert.assertEquals(to.getNumber(), to.getNumber());
        Assert.assertEquals(from.getUsername(), fromAfter.getUsername());
        this.testIfTransactionHistoryEmpty();
    }


    public void testTransferFromNonExistableAccount(String path) throws Exception {
        long accFrom = -1;
        long accTo = 4;
        BigDecimal amount = new BigDecimal(30);
        Optional<Account> optionalFrom = accountDao.findByNumber(accFrom);
        Optional<Account> optionalTo = accountDao.findByNumber(accTo);
        Assert.assertTrue(optionalTo.isPresent());
        Assert.assertFalse(optionalFrom.isPresent());
        Account to = optionalTo.get();

        AssertUtil.assertEquals(new BigDecimal(500), to.getBalance());

        String fullPath = path + accFrom + "/" + accTo + "/" + amount;

        URI uri = builder.setPath(fullPath).build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        System.out.println(jsonString);
        Assert.assertEquals("account with number -1 not found in DB", jsonString);

        Optional<Account> optionalAfterTo = accountDao.findByNumber(accTo);
        Assert.assertTrue(optionalAfterTo.isPresent());
        Account toAfter = optionalAfterTo.get();

        AssertUtil.assertEquals(new BigDecimal(500), toAfter.getBalance());
        Assert.assertEquals(to.getNumber(), to.getNumber());
        this.testIfTransactionHistoryEmpty();
    }


    public void testTransferToNonExistableAccount(String path) throws Exception {
        long accFrom = 3;
        long accTo = -1;
        BigDecimal amount = BigDecimal.ZERO;
        Optional<Account> optionalFrom = accountDao.findByNumber(accFrom);
        Optional<Account> optionalTo = accountDao.findByNumber(accTo);
        Assert.assertFalse(optionalTo.isPresent());
        Assert.assertTrue(optionalFrom.isPresent());
        Account from = optionalFrom.get();
        AssertUtil.assertEquals(new BigDecimal(100), from.getBalance());

        String fullPath = path + accFrom + "/" + accTo + "/" + amount;

        URI uri = builder.setPath(fullPath).build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        System.out.println(jsonString);
        Assert.assertEquals("account with number 3 not found in DB", jsonString);

        Optional<Account> optionalAfterFrom = accountDao.findByNumber(accFrom);
        Assert.assertTrue(optionalAfterFrom.isPresent());
        Account fromAfter = optionalAfterFrom.get();

        AssertUtil.assertEquals(new BigDecimal(100), fromAfter.getBalance());
        Assert.assertEquals(from.getNumber(), fromAfter.getNumber());
        Assert.assertEquals(from.getUsername(), fromAfter.getUsername());
        this.testIfTransactionHistoryEmpty();
    }


    private void testIfTransactionHistoryEmpty() throws URISyntaxException, IOException {
        URI uri = builder.setPath("/api/transaction-history").build();
        HttpGet thRequest = new HttpGet(uri);
        HttpResponse thResponse = client.execute(thRequest);
        int statusCode = thResponse.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);

        String jsonString = EntityUtils.toString(thResponse.getEntity());
        TransactionHistory[] responseTransactionHistories = mapper.readValue(jsonString, TransactionHistory[].class);
        Assert.assertEquals(0, responseTransactionHistories.length);
    }
}
