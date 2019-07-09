package com.revolut.controller;

import com.revolut.dao.DaoFactory;
import com.revolut.dao.TransactionHistoryDao;
import com.revolut.model.TransactionHistory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Account controller provides REST-API for working with account
 */
@Path("/transaction-history")
public class TransactionHistoryController {

    private TransactionHistoryDao transactionHistoryDao = DaoFactory.getInstance().getTransactionHistoryDao();

    /**
     * Method finds all transaction history
     * @return List&lt;{@link TransactionHistory}>
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TransactionHistory> findAll() {
        return transactionHistoryDao.findAll();
    }



}
