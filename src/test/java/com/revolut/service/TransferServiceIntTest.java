package com.revolut.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URI;

public class TransferServiceIntTest extends StartUpService {

    @Test
    public void testPositiveTransferMoney() throws Exception {
        URI uri = builder.setPath("/api/transfer-money/3/4/30").build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
    }

    @Test
    public void testNotEnoughMoneyTransferMoney() throws Exception {
        URI uri = builder.setPath("/api/transfer-money/1/2/10000000").build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
    }

    @Test
    public void testTransferMoneyWithinOneAccount() throws Exception {
        URI uri = builder.setPath("/api/transfer-money/1/1/20").build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
    }

    @Test
    public void testTransferNegativeAmountOfMoneyAccount() throws Exception {
        URI uri = builder.setPath("/api/transfer-money/1/4/-20").build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
    }

    @Test
    public void testTransferZeroAmountOfMoney() throws Exception {
        URI uri = builder.setPath("/api/transfer-money/1/4/0").build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
    }

    @Test
    public void testTransferFromNonExistableAccount() throws Exception {
        URI uri = builder.setPath("/api/transfer-money/-1/4/5").build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
    }

    @Test
    public void testTransferToNonExistableAccount() throws Exception {
        URI uri = builder.setPath("/api/transfer-money/1/-4/5").build();
        HttpPost request = new HttpPost(uri);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), statusCode);
    }
}
