package com.maven.bank.bankingservices;

import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.entities.LoanRequest;
import com.maven.bank.exceptions.MavenBankLoanException;

public interface LoanService {
    public LoanRequest approveLoanRequest(Account loanAccount) throws MavenBankLoanException;

    public LoanRequest approveLoanRequest(Customer customer, Account accountSeekingLoan) throws
            MavenBankLoanException;
}
