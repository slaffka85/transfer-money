package com.revolut.dao;

import com.revolut.model.Account;
import com.revolut.model.TransactionHistory;

import java.util.List;

/**
 * transfer transaction data access object
 *
 * @author v.tsapaev
 */
public interface TransactionHistoryDao {

    /**
     * method safe current transaction
     * @param transaction is current transaction
     */
    void save(TransactionHistory transaction);

    /**
     * method find all transaction history
     * @return List&lt;{@link TransactionHistory}>
     */
    List<TransactionHistory> findAll();
}
