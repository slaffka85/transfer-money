package com.revolut.controller;

import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.model.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Account controller provides REST-API for working with account
 */
@Path("/account")
public class AccountController {

    private AccountDao accountDao = DaoFactory.getInstance().getAccountDao();

    /**
     * Method finds all accounts
     * @return List&lt;{@link Account}>
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> findAll() {
        return accountDao.findAll();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Account save(Account account) {
        return accountDao.save(account);
    }

    /**
     * Method finds account by account number
     * @param accNumber is account number
     * @return List&lt;{@link Account}>
     */
    @GET
    @Path("/{accNumber}")
    public Account findByAccNumber(@PathParam("accNumber") long accNumber) {
        return accountDao.findByNumber(accNumber).orElseThrow(() -> new AccountNotFoundException(accNumber));
    }

    /**
     * Method finds account by username
     * @param username is name of user
     * @return List&lt;{@link Account}>
     */
    @GET
    @Path("/username/{username}")
    public List<Account> findByUsername(@PathParam("username") String username) {
        return accountDao.findByUsername(username);
    }

}
