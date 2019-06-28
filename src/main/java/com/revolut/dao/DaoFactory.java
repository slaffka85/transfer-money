package com.revolut.dao;

/**
 * Data access object factory
 *
 * @author v.tsapaev
 */
public abstract class DaoFactory {
    private static DaoFactory instance = new JdbcH2DaoFactory();

    /**
     *
     */
    DaoFactory() {}

    /**
     * Instance getter.
     *
     * @return DAOFactory
     */
    public static  DaoFactory getInstance() {
        return instance;
    }

    /**
     * Method throws exception.
     *
     * @return Nothing
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * method for initialisation db
     */
    public abstract void initDb();

    /**
     * getter for {@link AccountDao}
     * @return {@link AccountDao}
     */
    public abstract AccountDao getAccountDao();

    /**
     * getter for {@link TransactionHistoryDao}
     * @return {@link TransactionHistoryDao}
     */
    public abstract TransactionHistoryDao getTransactionHistoryDao();
}
