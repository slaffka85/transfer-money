package com.revolut.service;

import com.revolut.helper.HttpProcessTransfer;
import com.revolut.model.Account;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrencyTransferServiceIntTest extends StartUpService {

    private static final int THREAD_COUNT = 100;
    private static final int MILLIS_IN_NANO = 1000000;
    private static Logger logger = LogManager.getLogger(ConcurrencyTransferServiceIntTest.class.getName());

    @Test
    public void testTransferService() throws Exception {
        BigDecimal sumBefore = accountDAO.findAll()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        int firstAccountNumber = 1;
        List<Callable<HttpResponse>> processes = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            long accNumberFrom = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS);
            long accNumberTo = 0;
            while (accNumberFrom == accNumberTo || accNumberTo == 0) {
                accNumberTo = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS);
            }
            BigDecimal amount = new BigDecimal(random.nextInt(10) + 1);
            HttpProcessTransfer processTransfer = new HttpProcessTransfer(client, builder, accNumberFrom, accNumberTo, amount);
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

        BigDecimal sumAfter = accountDAO.findAll()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        Assert.assertEquals(0, sumBefore.compareTo(sumAfter));
    }

    @Test
    public void testTransferMoneyWithSomeErrors() throws Exception {
        BigDecimal sumBefore = accountDAO.findAll()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        int firstAccountNumber = 1;
        List<Callable<HttpResponse>> processes = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            long accNumberFrom = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS + 1);
            long accNumberTo = firstAccountNumber + random.nextInt(COUNT_OF_ACCOUNTS + 1);
            BigDecimal amount = new BigDecimal(random.nextInt(100));
            HttpProcessTransfer processTransfer = new HttpProcessTransfer(client, builder, accNumberFrom, accNumberTo, amount);
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

        BigDecimal sumAfter = accountDAO.findAll()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        Assert.assertEquals(0, sumBefore.compareTo(sumAfter));
    }
}
