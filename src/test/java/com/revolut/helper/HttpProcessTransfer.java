package com.revolut.helper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

public class HttpProcessTransfer implements Callable<HttpResponse> {

    private static Logger logger = LogManager.getLogger(HttpProcessTransfer.class);
    private long accNumberFrom;
    private long accNumberTo;
    private BigDecimal amount;
    private HttpClient client;
    private URIBuilder builder;

    public HttpProcessTransfer(HttpClient client, URIBuilder builder, long accNumberFrom, long accNumberTo, BigDecimal amount) {
        this.client = client;
        this.builder = builder;
        this.accNumberFrom = accNumberFrom;
        this.accNumberTo = accNumberTo;
        this.amount = amount;
    }

    @Override
    public HttpResponse call() throws Exception {
        builder.setPathSegments("api", "transfer-money", Long.toString(accNumberFrom), Long.toString(accNumberTo), amount.toString());
        HttpPost request = new HttpPost(builder.toString());
        HttpResponse response = client.execute(request);
        return response;
    }
}
