package com.revolut.controller;

import com.revolut.dao.AccountDao;
import com.revolut.dao.DaoFactory;
import com.revolut.exception.AccountAlreadyExistsException;
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

    /**
     * Method creates account
     * @param account is account
     * @return created {@link Account}
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Account save(Account account) {
        if (account.getNumber() <= 0) {
            throw new IllegalArgumentException("it's impossible to save account because number must be positive value");
        }
        accountDao.findByNumber(account.getNumber()).ifPresent(acc -> {
            throw new AccountAlreadyExistsException(acc.getNumber());
        });

        return accountDao.save(account);
    }

    /**
     * Method finds account by account number
     * @param accNumber is account number
     * @return List&lt;{@link Account}>
     */
    @GET
    @Path("/acc-number/{accNumber}")
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
