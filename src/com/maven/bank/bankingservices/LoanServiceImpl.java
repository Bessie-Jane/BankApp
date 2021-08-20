package com.maven.bank.bankingservices;

import com.maven.bank.datastore.LoanRequestStatus;
import com.maven.bank.datastore.LoanStatus;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.entities.LoanRequest;
import com.maven.bank.exceptions.MavenBankLoanException;

import java.math.BigDecimal;

public class LoanServiceImpl implements LoanService{


    @Override
    public LoanRequest approveLoanRequest(Account accountSeekingLoan) throws MavenBankLoanException {
        if(accountSeekingLoan == null){
            throw new MavenBankLoanException("An account is required to process the loan request");
        }
        if(accountSeekingLoan.getAccountLoanRequest() == null){
            throw new MavenBankLoanException("No loan request provided for processing");
        }
        LoanRequest theLoanRequest = accountSeekingLoan.getAccountLoanRequest();
        theLoanRequest.setLoanStatus(decideOnLoanRequest(accountSeekingLoan));

        return theLoanRequest;
    }

    @Override
    public LoanRequest approveLoanRequest(Customer customer, Account accountSeekingForLoan) throws MavenBankLoanException {
        LoanRequestStatus decision = decideOnLoanWithCustomerBalance(customer, accountSeekingForLoan);
        LoanRequest theLoanRequest = accountSeekingForLoan.getAccountLoanRequest();
        theLoanRequest.setLoanStatus(decision);

        if(decision != LoanRequestStatus.APPROVED){
           theLoanRequest = approveLoanRequest(accountSeekingForLoan);
        }
        return theLoanRequest;
    }


    private LoanRequestStatus decideOnLoanRequestWithAccountBalance(Account accountSeekingLoan) throws MavenBankLoanException{

        LoanRequestStatus decision = LoanRequestStatus.PENDING;
        LoanRequest theLoanRequest = accountSeekingLoan.getAccountLoanRequest();
        BigDecimal loanAmountAppliedFor = theLoanRequest.getLoanAmount();
        BigDecimal bankAccountBalancePercentage = BigDecimal.valueOf(0.2);

        BigDecimal loanAmountApprovedAutomatically = accountSeekingLoan.getBalance().
                multiply(bankAccountBalancePercentage);

        if(loanAmountAppliedFor.compareTo(loanAmountApprovedAutomatically) < BigDecimal.ZERO.intValue()){
            decision = LoanRequestStatus.APPROVED;
        }

        return decision;
    }

    private LoanRequestStatus decideOnLoanWithCustomerBalance(Customer customer, Account accountSeekingLoan)throws
            MavenBankLoanException{
        LoanRequestStatus decision = LoanRequestStatus.PENDING;
        BigDecimal relationshipVolume = BigDecimal.valueOf(0.2);

        BigDecimal totalCustomerBalance = BigDecimal.ZERO;
        if(customer.getAccounts().size() > BigDecimal.ONE.intValue()){
            for(Account customerAccount : customer.getAccounts()){
                totalCustomerBalance = totalCustomerBalance.add(customerAccount.getBalance());
            }
        }
        BigDecimal loanAmountApprovedAutomatically = totalCustomerBalance.multiply(relationshipVolume);
        if(accountSeekingLoan.getAccountLoanRequest().getLoanAmount().compareTo
                (loanAmountApprovedAutomatically) < BigDecimal.ZERO.intValue()){
            decision = LoanRequestStatus.APPROVED;
        }
        return decision;
    }

    private LoanRequestStatus decideOnLoanRequest(Account accountSeekingLoan) throws MavenBankLoanException{
        LoanRequestStatus decision = decideOnLoanRequestWithAccountBalance(accountSeekingLoan);

        return decision;
    }
}
