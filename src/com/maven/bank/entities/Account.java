package com.maven.bank.entities;

import com.maven.bank.datastore.AccountType;
import java.math.BigDecimal;
import java.util.Calendar;

public class Account {
    private long accountNumber;
    private AccountType typeOfAccount;
    private BigDecimal balance = BigDecimal.ZERO;
    private String accountPin;

    public  Account(){}

    public Account(long accountNumber, AccountType type){
        this.accountNumber = accountNumber;
        typeOfAccount = type;
    }
    public Account(long accountNumber, AccountType type, BigDecimal balance){
        this(accountNumber, type);
        this.balance = balance;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(AccountType typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountPin() {
        return accountPin;
    }

    public void setAccountPin(String accountPin) {
        this.accountPin = accountPin;
    }

    public Calendar getAccount() {
        return null;
    }
}
