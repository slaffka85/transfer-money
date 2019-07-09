package com.revolut.service;

import java.math.BigDecimal;

public interface TransferService {

    void transferMoney(long accNumberFrom, long accNumberTo, BigDecimal amount);
}
