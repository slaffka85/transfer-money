package com.revolut.rest.concurrency;

import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.helper.AbstractHttpProcessTransfer;
import com.revolut.model.Account;
import com.revolut.rest.IntTest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;

import javax.ws.rs.core.Response;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class AbstractHttpTransferServiceIntTest<T> extends IntTest {
    protected static final int THREAD_COUNT = 100;
    protected static final int MILLIS_IN_NANO = 1000000;
    protected static final int COUNT_OF_ACCOUNTS = 10;
    protected static AccountDao accountDao = DaoFactory.getInstance().getAccountDao();
    protected static Random random = new Random();


    protected static void initDB() {
        DaoFactory.getInstance().initDb();
        createAccounts();
    }

    private static void createAccounts() {
        logger.debug(String.format("creation accounts with number 1..%d", 10));

        for (int i = 1; i <= 10; i++) {
            BigDecimal balance = new BigDecimal(20000 + new Random().nextInt(50000));
            String username = String.format("test%d", i);
            Account account = new Account(i, username, balance);
            DaoFactory.getInstance().getAccountDao().save(account);
        }
        logger.debug(String.format("creation accounts with number 1..%d completed", 10));
    }



    public void testTransferMoney() throws Exception {
        BigDecimal sumBefore = getSumOfAccount();
        int firstAccountNumber = 1;
        List<Callable<HttpResponse>> processes = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            long accNumberFrom = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS);
            long accNumberTo = 0;
            while (accNumberFrom == accNumberTo || accNumberTo == 0) {
                accNumberTo = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS);
            }
            BigDecimal amount = new BigDecimal(random.nextInt(10) + 1);
            AbstractHttpProcessTransfer processTransfer = getProcessTransfer(accNumberFrom, accNumberTo, amount);
            processes.add(processTransfer);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        long startTime = System.nanoTime();
        List<Future<HttpResponse>> futures = executorService.invokeAll(processes);

        executorService.shutdown();
        long endTime = System.nanoTime();
        logger.info(String.format("%d processes executed for %d millis", THREAD_COUNT, (endTime - startTime) / MILLIS_IN_NANO));
        futures.forEach(future -> {
            try {
                HttpResponse response = future.get();
                Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        BigDecimal sumAfter = getSumOfAccount();
        Assert.assertEquals(0, sumBefore.compareTo(sumAfter));
    }

    private BigDecimal getSumOfAccount() {
        return accountDao.findAll()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public void testTransferMoneyWithSomeErrors() throws Exception {

        BigDecimal sumBefore = getSumOfAccount();
        int firstAccountNumber = 1;
        List<Callable<HttpResponse>> processes = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            long accNumberFrom = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS + 1);
            long accNumberTo = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS + 1);
            BigDecimal amount = new BigDecimal(random.nextInt(100));
            AbstractHttpProcessTransfer processTransfer = getProcessTransfer(accNumberFrom, accNumberTo, amount);
            processes.add(processTransfer);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        long startTime = System.nanoTime();
        List<Future<HttpResponse>> futures = executorService.invokeAll(processes);

        executorService.shutdown();
        long endTime = System.nanoTime();
        logger.info(String.format("%d processes executed for %d millis", THREAD_COUNT, (endTime - startTime) / MILLIS_IN_NANO));
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        BigDecimal sumAfter = getSumOfAccount();

        Assert.assertEquals(0, sumBefore.compareTo(sumAfter));
    }

    private AbstractHttpProcessTransfer getProcessTransfer(long accNumberFrom, long accNumberTo, BigDecimal amount) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Class<T> tClass = (Class<T>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        Constructor constructor = tClass.getDeclaredConstructor(HttpClient.class, URIBuilder.class, Long.class, Long.class, BigDecimal.class);
        return (AbstractHttpProcessTransfer) constructor.newInstance(client, builder, accNumberTo, accNumberFrom, amount);
    }
}
