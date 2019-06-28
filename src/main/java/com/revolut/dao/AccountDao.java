package com.revolut.dao;

import com.revolut.model.Account;

import java.util.List;
import java.util.Optional;

/**
 * user's account data access object
 *
 * @author v.tsapaev
 */
public interface AccountDao {

    /**
     * Method saves user account into DB
     * @param account is user account
     */
    Account save(Account account);


    /**
     * Method find all accounts from DB
     * @return List&lt;Account>
     */
    List<Account> findAll();

    /**
     * Method finds account by number
     * @param number is number of account
     * @return Optional&lt;Account>
     */
    Optional<Account> findByNumber(long number);

    /**
     * Method finds account by username
     * @param username is name of user
     * @return List&lt;Account>
     */
    List<Account> findByUsername(String username);

    /**
     * Method update accounts in batch.
     * @param accounts
     */
    void update(Account... accounts);

}
