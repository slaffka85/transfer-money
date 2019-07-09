package com.revolut.rest;

import org.junit.Before;
import org.junit.Test;

public class TransferMoneyLockControllerIntTest extends AbstractTransferMoneyControllerIntTest {

    private static final String path = "/api/transfer-money-lock/";

    @Before
    public void initDb() {
        super.initDb();
    }

    @Test
    public void testPositiveTransferMoney() throws Exception {
        super.testPositiveTransferMoney(path);
    }

    @Test
    public void testNotEnoughMoneyTransferMoney() throws Exception {
        super.testNotEnoughMoneyTransferMoney(path);
    }

    @Test
    public void testTransferMoneyWithinOneAccount() throws Exception {
        super.testTransferMoneyWithinOneAccount(path);
    }

    @Test
    public void testTransferNegativeAmountOfMoneyAccount() throws Exception {
        super.testTransferNegativeAmountOfMoneyAccount(path);
    }

    @Test
    public void testTransferZeroAmountOfMoney() throws Exception {
        super.testTransferZeroAmountOfMoney(path);
    }

    @Test
    public void testTransferFromNonExistableAccount() throws Exception {
        super.testTransferFromNonExistableAccount(path);
    }

    @Test
    public void testTransferToNonExistableAccount() throws Exception {
        super.testTransferToNonExistableAccount(path);
    }
}
