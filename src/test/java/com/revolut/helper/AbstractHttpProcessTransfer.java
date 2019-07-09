package com.revolut.helper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public abstract class AbstractHttpProcessTransfer implements Callable<HttpResponse> {

    private HttpClient client;
    protected Logger logger = LogManager.getLogger(this.getClass().getName());

    protected URIBuilder builder;

    public AbstractHttpProcessTransfer(HttpClient client, URIBuilder builder) {
        this.client = client;
        this.builder = builder;
    }

    @Override
    public HttpResponse call() throws Exception {
        logger.debug("url = " + builder.toString());
        HttpPost request = new HttpPost(builder.toString());
        HttpResponse response = client.execute(request);
        return response;
    }
}
