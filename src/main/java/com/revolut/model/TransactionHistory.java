package com.revolut.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * model of transfer transaction
 */
public class TransactionHistory {

    private long accNumberFrom;

    private BigDecimal balanceBeforeFrom;

    private BigDecimal balanceAfterFrom;

    private long accNumberTo;

    private BigDecimal balanceBeforeTo;

    private BigDecimal balanceAfterTo;

    private BigDecimal amount;

    private LocalDateTime date;

    public TransactionHistory() {
    }

    public TransactionHistory(
            long accNumberFrom,
            BigDecimal balanceBeforeFrom,
            BigDecimal balanceAfterFrom,
            long accNumberTo,
            BigDecimal balanceBeforeTo,
            BigDecimal balanceAfterTo,
            BigDecimal amount,
            LocalDateTime date
    ) {
        this.accNumberFrom = accNumberFrom;
        this.balanceBeforeFrom = balanceBeforeFrom;
        this.balanceAfterFrom = balanceAfterFrom;
        this.accNumberTo = accNumberTo;
        this.balanceBeforeTo = balanceBeforeTo;
        this.balanceAfterTo = balanceAfterTo;
        this.amount = amount;
        this.date = date;
    }


    public long getAccNumberFrom() {
        return accNumberFrom;
    }

    public void setAccNumberFrom(long accNumberFrom) {
        this.accNumberFrom = accNumberFrom;
    }

    public BigDecimal getBalanceBeforeFrom() {
        return balanceBeforeFrom;
    }

    public void setBalanceBeforeFrom(BigDecimal balanceBeforeFrom) {
        this.balanceBeforeFrom = balanceBeforeFrom;
    }

    public BigDecimal getBalanceAfterFrom() {
        return balanceAfterFrom;
    }

    public void setBalanceAfterFrom(BigDecimal balanceAfterFrom) {
        this.balanceAfterFrom = balanceAfterFrom;
    }

    public long getAccNumberTo() {
        return accNumberTo;
    }

    public void setAccNumberTo(long accNumberTo) {
        this.accNumberTo = accNumberTo;
    }

    public BigDecimal getBalanceBeforeTo() {
        return balanceBeforeTo;
    }

    public void setBalanceBeforeTo(BigDecimal balanceBeforeTo) {
        this.balanceBeforeTo = balanceBeforeTo;
    }

    public BigDecimal getBalanceAfterTo() {
        return balanceAfterTo;
    }

    public void setBalanceAfterTo(BigDecimal balanceAfterTo) {
        this.balanceAfterTo = balanceAfterTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "accNumberFrom=" + accNumberFrom +
                ", balanceBeforeFrom=" + balanceBeforeFrom +
                ", balanceAfterFrom=" + balanceAfterFrom +
                ", accNumberTo=" + accNumberTo +
                ", balanceBeforeTo=" + balanceBeforeTo +
                ", balanceAfterTo=" + balanceAfterTo +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
