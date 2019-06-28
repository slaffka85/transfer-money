package com.revolut.dao.impl;

import com.revolut.dao.AccountDao;
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

    private static Logger log = LogManager.getLogger(JdbcH2AccountDaoImpl.class.getName());

    private final JdbcConnectionPool jdbcConnectionPool;

    public JdbcH2AccountDaoImpl(JdbcConnectionPool jdbcConnectionPool) {
        this.jdbcConnectionPool = jdbcConnectionPool;
    }

    /**
     * Method saves user accounts {@link Account}into DB
     * @param account are user accounts
     */
    @Override
    public Account save(Account account) {
        try (Connection connection = jdbcConnectionPool.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("insert into account (acc_number, username, balance) values (?, ?, ?)");
            stmt.setLong(1, account.getNumber());
            stmt.setString(2, account.getUsername());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.executeUpdate();
            connection.commit();
            if (log.isDebugEnabled()) {
                log.debug("account with number " + account.getNumber() + " has been saved to db");
            }
        } catch (SQLException e){
            log.warn("account with number " + account.getNumber() + " hasn't been saved", e);
        }
        return account;
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
            ResultSet resultSet = stmt.executeQuery("select acc_number, username, balance from account ");
            while (resultSet.next()) {
                Account account = new Account(
                        resultSet.getLong("acc_number"),
                        resultSet.getString("username"),
                        resultSet.getBigDecimal("balance")
                );
                accountList.add(account);
            }
        } catch (SQLException e){
            log.debug("accounts haven't been found ", e);
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
            PreparedStatement stmt = connection.prepareStatement("select acc_number, username, balance from account where acc_number = ? ");
            stmt.setLong(1, number);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                account = new Account(
                        resultSet.getLong("acc_number"),
                        resultSet.getString("username"),
                        resultSet.getBigDecimal("balance")
                );
            }
        } catch (SQLException e){
            log.debug(String.format("account with number %d hasn't been found", number), e);
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
            PreparedStatement stmt = connection.prepareStatement("select acc_number, username, balance from account where USERNAME = ? ");
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                Account account = new Account(
                        resultSet.getLong("acc_number"),
                        resultSet.getString("username"),
                        resultSet.getBigDecimal("balance")
                );
                accounts.add(account);
            }
        } catch (SQLException e){
            log.debug(String.format("account for user %s hasn't been found", username), e);
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
            PreparedStatement stmt = connection.prepareStatement("update account set balance = ? where acc_number = ?");
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
                log.debug("account with number " + builder.toString() + " has been updated");
            }
        } catch (SQLException e){
            DbUtil.rollbackQuietly(connection);
            log.warn("accounts haven't been updated ", e);
        } finally {
            DbUtil.setAutoCommitTrueAndCloseQuietly(connection);
        }
    }


}
