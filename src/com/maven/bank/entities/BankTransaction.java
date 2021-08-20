package com.maven.bank.entities;

import com.maven.bank.bankingservices.BankService;
import com.maven.bank.datastore.BankTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankTransaction {
    private long id;
    private LocalDateTime dateTime;
    private BankTransactionType type;
    private BigDecimal amount;

   public BankTransaction(BankTransactionType type, BigDecimal txAmount){
        this.id = BankService.generateTransactionNumber();
        this.dateTime = LocalDateTime.now();
        this.type = type;
        amount = txAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BankTransactionType getType() {
        return type;
    }

    public void setType(BankTransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
