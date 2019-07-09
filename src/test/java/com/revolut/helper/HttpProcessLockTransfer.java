package com.revolut.helper;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;

import java.math.BigDecimal;

public class HttpProcessLockTransfer extends AbstractHttpProcessTransfer {

    public HttpProcessLockTransfer(HttpClient client, URIBuilder builder, Long accNumberFrom, Long accNumberTo, BigDecimal amount) {
        super(client, builder);
        this.builder.setPathSegments("api", "transfer-money-lock", Long.toString(accNumberFrom), Long.toString(accNumberTo), amount.toString());
    }
}
