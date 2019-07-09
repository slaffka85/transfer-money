package com.revolut.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.helper.AssertUtil;
import com.revolut.model.Account;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Stream;

public class AccountControllerIntTest extends IntTest {


    private ObjectMapper mapper = new ObjectMapper();
    private static AccountDao accountDao;

    @BeforeClass
    public static void initDb() {
        DaoFactory.getInstance().initDb();
        Account account = new Account(1, "test1", new BigDecimal(100));
        accountDao = DaoFactory.getInstance().getAccountDao();
        accountDao.save(account);
    }

    @Test
    public void testFindAll() throws URISyntaxException, IOException {
        URI uri = builder.setPath("/api/account").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
        String jsonString = EntityUtils.toString(response.getEntity());
        Account[] responseAccounts = mapper.readValue(jsonString, Account[].class);
        Assert.assertTrue(responseAccounts.length >= 1);
        Optional<Account> optionalExistedAccount = Stream.of(responseAccounts).filter(account -> 1 == account.getNumber()).findFirst();
        Assert.assertTrue(optionalExistedAccount.isPresent());
        Account existedAccount = optionalExistedAccount.get();
        Assert.assertEquals(1, existedAccount.getNumber());
        AssertUtil.assertEquals(new BigDecimal(100), existedAccount.getBalance());
        Assert.assertEquals("test1", existedAccount.getUsername());
    }

    @Test
    public void testSuccessfulCreateAccount() throws Exception {
        URI uri = builder.setPath("/api/account").build();
        Account account = new Account(2, "test2", new BigDecimal(100));

        String jsonInString = mapper.writeValueAsString(account);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);

        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Account responseAccount = mapper.readValue(jsonString, Account.class);
        assertEqualsAccount(account, responseAccount);

        Optional<Account> optionalAccountInDb = accountDao.findByNumber(2);
        Assert.assertTrue(optionalAccountInDb.isPresent());
        Account accountInDb = optionalAccountInDb.get();
        assertEqualsAccount(account, accountInDb);
    }

    @Test
    public void testCreateAlreadyExistedAccount() throws Exception {
        URI uri = builder.setPath("/api/account").build();
        Account account = new Account(1, "test1", new BigDecimal(100));

        String jsonInString = mapper.writeValueAsString(account);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);

        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("account with number 1 already exists", jsonString);
    }

    @Test
    public void testCreateAccountWithoutNumber() throws Exception {
        URI uri = builder.setPath("/api/account").build();
        Account account = new Account(-1, "test1", new BigDecimal(100));

        String jsonInString = mapper.writeValueAsString(account);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);

        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);

        String jsonString = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("it's impossible to save account because number must be positive value", jsonString);
    }




    private void assertEqualsAccount(Account expectedAccount, Account actualAccount) {
        Assert.assertEquals(expectedAccount.getNumber(), actualAccount.getNumber());
        Assert.assertEquals(expectedAccount.getUsername(), actualAccount.getUsername());
        AssertUtil.assertEquals(expectedAccount.getBalance(), actualAccount.getBalance());
    }
}
