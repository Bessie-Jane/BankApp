package com.maven.bank.bankingservices;

import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.datastore.AccountType;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientFundsException;
import com.maven.bank.exceptions.MavenBankTransactionException;

import java.math.BigDecimal;

public interface AccountService {

    public long openAccount(Customer theCustomer, AccountType type) throws MavenBankException;

    public BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException, MavenBankException;

    public Account findAccount(long accountNumber)throws MavenBankException;

    public Account findAccount(Customer customer, long accountNumber)throws MavenBankException;

    public BigDecimal withdraw(BigDecimal amount, long accountNumber) throws MavenBankException,
            MavenBankInsufficientFundsException;

    public void applyForOverDraft(Account theAccount);

}
