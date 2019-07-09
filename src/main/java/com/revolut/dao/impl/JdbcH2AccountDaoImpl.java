package com.revolut.dao.impl;

import com.revolut.dao.AccountDao;
import com.revolut.exception.JdbcException;
import com.revolut.model.Account;
import com.revolut.util.DbUtil;
import com.revolut.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User Account data access object implementation
 */
public class JdbcH2AccountDaoImpl implements AccountDao {

    private static final String ACCOUNT_WITH_NUMBER = "account with number";
    private static final String SAVE_EXCEPTION_MESSAGE = ACCOUNT_WITH_NUMBER + " %d hasn't been saved";
    private static final String SAVE_SUCCESS_MESSAGE = ACCOUNT_WITH_NUMBER + " %d has been saved to db";
    private static final String ACC_NUMBER_FIELD = "acc_number";
    private static final String USERNAME_FIELD = "username";
    private static final String BALANCE_FIELD = "balance";

    private static final String SQL_SAVE_ACCOUNT = "insert into account (acc_number, username, balance) values (?, ?, ?)";
    private static final String SQL_FIND_ALL = "select acc_number, username, balance from account ";
    private static final String SQL_FIND_ACCOUNT_BY_NUMBER = "select acc_number, username, balance from account where acc_number = ? ";
    private static final String SQL_FIND_ACCOUNTS_BY_USERNAME = "select acc_number, username, balance from account where USERNAME = ? ";
    private static final String SQL_UPDATE_ACCOUNT = "update account set balance = ? where acc_number = ?";
    private static Logger log = LogManager.getLogger(JdbcH2AccountDaoImpl.class.getName());

    private final JdbcConnectionPool jdbcConnectionPool;

    public JdbcH2AccountDaoImpl(JdbcConnectionPool jdbcConnectionPool) {
        this.jdbcConnectionPool = jdbcConnectionPool;
    }

    /**
     * Method saves user accounts {@link Account}into DB
     * @param account are user accounts
     * @return account if account has been saved
     */
    @Override
    public Account save(Account account) {
        try (Connection connection = jdbcConnectionPool.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(SQL_SAVE_ACCOUNT);
            stmt.setLong(1, account.getNumber());
            stmt.setString(2, account.getUsername());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.executeUpdate();
            connection.commit();
            if (log.isDebugEnabled()) {
                log.debug(String.format(SAVE_SUCCESS_MESSAGE, account.getNumber()));
            }
            return account;
        } catch (SQLException e){
            String message = String.format(SAVE_EXCEPTION_MESSAGE, account.getNumber());
            throw new JdbcException(message, e);
        }
    }


    /**
     * Method find all accounts from DB
     * @return List&lt;{@link Account}>
     */
    @Override
    public List<Account> findAll() {
        List<Account> accountList = new ArrayList<>();
        try (Connection connection = jdbcConnectionPool.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(SQL_FIND_ALL);
            while (resultSet.next()) {
                Account account = new Account(
                        resultSet.getLong(ACC_NUMBER_FIELD),
                        resultSet.getString(USERNAME_FIELD),
                        resultSet.getBigDecimal(BALANCE_FIELD)
                );
                accountList.add(account);
            }
        } catch (SQLException e){
            String message = "accounts haven't been found";
            throw new JdbcException(message, e);
        }
        return accountList;
    }

    /**
     * Method finds account by number
     * @param number is number of account
     * @return Optional&lt;{@link Account}>
     */
    @Override
    public Optional<Account> findByNumber(long number) {
        Account account = null;
        try (Connection connection = jdbcConnectionPool.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(SQL_FIND_ACCOUNT_BY_NUMBER);
            stmt.setLong(1, number);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                account = new Account(
                        resultSet.getLong(ACC_NUMBER_FIELD),
                        resultSet.getString(USERNAME_FIELD),
                        resultSet.getBigDecimal(BALANCE_FIELD)
                );
            }
        } catch (SQLException e){
            String message = String.format(ACCOUNT_WITH_NUMBER + " %d hasn't been found", number);
            throw new JdbcException(message, e);
        }
        return Optional.ofNullable(account);
    }

    /**
     * Method finds account by username
     * @param username is name of user
     * @return List&lt;{@link Account}>
     */
    @Override
    public List<Account> findByUsername(String username) {
        List<Account> accounts = new ArrayList<>();
        try (Connection connection = jdbcConnectionPool.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(SQL_FIND_ACCOUNTS_BY_USERNAME);
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                Account account = new Account(
                        resultSet.getLong(ACC_NUMBER_FIELD),
                        resultSet.getString(USERNAME_FIELD),
                        resultSet.getBigDecimal(BALANCE_FIELD)
                );
                accounts.add(account);
            }
        } catch (SQLException e){
            String message = String.format("account for user %s hasn't been found", username);
            throw new JdbcException(message, e);
        }
        return accounts;
    }

    /**
     * Method update accounts {@link Account} in batch. None of account will be updated if it's impossible to update at least one account
     * @param accounts are user accounts for update
     */
    @Override
    public void update(Account... accounts) {
        Connection connection = null;
        try {
            connection = jdbcConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE_ACCOUNT);
            StringBuilder builder = new StringBuilder();
            for (Account account: accounts) {
                builder.append(account.getNumber()).append(", ");
                stmt.setBigDecimal(1, account.getBalance());
                stmt.setLong(2, account.getNumber());
                stmt.addBatch();
                stmt.executeUpdate();
            }
            StringUtil.deleteLastCharacters(builder, ", ");
            connection.commit();
            if (log.isDebugEnabled()) {
                log.debug(ACCOUNT_WITH_NUMBER + " " + builder.toString() + " has been updated");
            }
        } catch (SQLException e){
            DbUtil.rollbackQuietly(connection);
            String message = "accounts haven't been updated.";
            throw new JdbcException(message, e);
        } finally {
            DbUtil.setAutoCommitTrueAndCloseQuietly(connection);
        }
    }


}
