package com.revolut.rest.concurrency;

import com.revolut.helper.HttpProcessSyncTransfer;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpTransferServiceSyncImplIntTest extends AbstractHttpTransferServiceIntTest<HttpProcessSyncTransfer> {


    @BeforeClass
    public static void initDB() {
        AbstractHttpTransferServiceIntTest.initDB();

    }


    @Test
    public void testTransferMoney() throws Exception {
        super.testTransferMoney();
    }

    @Test
    public void testTransferMoneyWithSomeErrors() throws Exception {
        super.testTransferMoneyWithSomeErrors();
    }

}
