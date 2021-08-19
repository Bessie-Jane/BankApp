package com.maven.bank.entities;

import com.maven.bank.datastore.LoanRequestStatus;
import com.maven.bank.datastore.LoanStatus;
import com.maven.bank.datastore.LoanType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Loan {
    private BigDecimal loanAmount;
    private LocalDateTime startDate;
    private int tenor;
    private double interestRate;
    private LoanType typeOfLoan;
    private LoanStatus status;

}
