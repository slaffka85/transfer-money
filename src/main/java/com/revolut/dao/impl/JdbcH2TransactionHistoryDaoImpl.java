package com.revolut.dao.impl;

import com.revolut.dao.TransactionHistoryDao;
import com.revolut.model.TransactionHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Account data access object implementation
 */
public class JdbcH2TransactionHistoryDaoImpl implements TransactionHistoryDao {

    private static final String ACC_NUMBER_FROM_FIELD = "acc_number_from";
    private static final String BALANCE_BEFORE_FROM_FIELD = "balance_before_from";
    private static final String BALANCE_AFTER_FROM_FIELD = "balance_after_from";
    private static final String ACC_NUMBER_TO_FIELD = "acc_number_to";
    private static final String BALANCE_BEFORE_TO_FIELD = "balance_before_to";
    private static final String BALANCE_AFTER_TO_FIELD = "balance_after_to";
    private static final String AMOUNT_FIELD = "amount";
    private static final String TRANSACTION_DATE_FIELD = "transaction_date";
    private static Logger log = LogManager.getLogger(JdbcH2TransactionHistoryDaoImpl.class.getName());

    private final JdbcConnectionPool jdbcConnectionPool;

    public JdbcH2TransactionHistoryDaoImpl(JdbcConnectionPool jdbcConnectionPool) {
        this.jdbcConnectionPool = jdbcConnectionPool;
    }

    /**
     * Method saves transfer transaction into DB
     * @param transaction is current transfer transaction
     */
    @Override
    public void save(TransactionHistory transaction) {
        try (Connection connection = jdbcConnectionPool.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "insert into transfer_transaction (" +
                            "acc_number_from, " +
                            "balance_before_from, " +
                            "balance_after_from, " +
                            "acc_number_to," +
                            "balance_before_to," +
                            "balance_after_to, " +
                            "amount, " +
                            "transaction_date" +
                            ") values (?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, transaction.getAccNumberFrom());
            stmt.setBigDecimal(2, transaction.getBalanceBeforeFrom());
            stmt.setBigDecimal(3, transaction.getBalanceAfterFrom());
            stmt.setLong(4, transaction.getAccNumberTo());
            stmt.setBigDecimal(5, transaction.getBalanceBeforeTo());
            stmt.setBigDecimal(6, transaction.getBalanceAfterTo());
            stmt.setBigDecimal(7, transaction.getAmount());
            stmt.setObject(8, transaction.getDate());
            stmt.executeUpdate();
        } catch (SQLException e){
            log.warn("transfer transaction hasn't been saved to DB", e);
        }
    }

    @Override
    public List<TransactionHistory> findAll() {
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        try (Connection connection = jdbcConnectionPool.getConnection()) {
            Statement stmt = connection.createStatement();
            String query = "select acc_number_from, " +
                    "balance_before_from, " +
                    "balance_after_from, " +
                    "acc_number_to, " +
                    "balance_before_to, " +
                    "balance_after_to, " +
                    "amount, " +
                    "transaction_date " +
                    "from transfer_transaction " +
                    "order by transaction_date desc ";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                TransactionHistory transactionHistory = new TransactionHistory();
                transactionHistory.setAccNumberFrom(rs.getLong(ACC_NUMBER_FROM_FIELD));
                transactionHistory.setBalanceBeforeFrom(rs.getBigDecimal(BALANCE_BEFORE_FROM_FIELD));
                transactionHistory.setBalanceAfterFrom(rs.getBigDecimal(BALANCE_AFTER_FROM_FIELD));

                transactionHistory.setAccNumberTo(rs.getLong(ACC_NUMBER_TO_FIELD));
                transactionHistory.setBalanceBeforeTo(rs.getBigDecimal(BALANCE_BEFORE_TO_FIELD));
                transactionHistory.setBalanceAfterTo(rs.getBigDecimal(BALANCE_AFTER_TO_FIELD));

                transactionHistory.setAmount(rs.getBigDecimal(AMOUNT_FIELD));
                transactionHistory.setDate(rs.getTimestamp(TRANSACTION_DATE_FIELD));

                transactionHistories.add(transactionHistory);
            }
        } catch (SQLException e){
            log.warn("some error", e);
        }
        return transactionHistories;
    }


}
