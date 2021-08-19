package com.maven.bank.bankingservices;

import com.maven.bank.datastore.CustomerRepo;
import com.maven.bank.datastore.LoanRequestStatus;
import com.maven.bank.datastore.LoanType;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.CurrentAccount;
import com.maven.bank.entities.Customer;
import com.maven.bank.entities.LoanRequest;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankLoanException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceImplTest {
    private LoanRequest johnLoanRequest;
    private LoanService loanService;
    private  AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
        loanService = new LoanServiceImpl();

        johnLoanRequest = new LoanRequest();
        johnLoanRequest.setApplyDate(LocalDateTime.now());
        johnLoanRequest.setTypeOfLoan(LoanType.SME);
        johnLoanRequest.setInterestRate(0.1);
        johnLoanRequest.setLoanStatus(LoanRequestStatus.NEW);
        johnLoanRequest.setTenor(24);
    }

    @AfterEach
    void tearDown() {
//        CustomerRepo.reset();
    }

    @Test
    void approveLoanRequestWithNullAccount(){
        assertThrows(MavenBankLoanException.class, ()-> loanService.approveLoanRequest(null) );
    }

    @Test
    void approveLoanRequestWithNullLoan(){
        CurrentAccount openCurrenAccountWithNull = new CurrentAccount();
        assertThrows(MavenBankLoanException.class, ()-> loanService.approveLoanRequest(openCurrenAccountWithNull));
    }

    @Test
    void approveLoanWithAccountBalance() throws MavenBankException {
        try{
            Account johnCurrentAccount = accountService. findAccount(2);
            assertNull(johnCurrentAccount.getAccountLoanRequest());
            johnLoanRequest.setLoanAmount(BigDecimal.valueOf(9000));
            johnCurrentAccount.setAccountLoanRequest(johnLoanRequest);

            LoanRequest processedLoanRequest = loanService.approveLoanRequest(johnCurrentAccount);
            assertEquals(LoanRequestStatus.APPROVED, processedLoanRequest.getLoanStatus());

        }catch(MavenBankException ex){
            ex.printStackTrace();
        }
    }

    @Test
    void approveLoanWithLengthOfRelationShip(){
        try{
            Account johnSavingsAccount = accountService. findAccount(1);
            Optional<Customer> optionalCustomer = CustomerRepo.getCustomers().values().stream().findFirst();
            Customer john = optionalCustomer.isPresent() ? optionalCustomer.get() : null;
            assertNotNull(john);
            john.setRelationshipStartDate(johnSavingsAccount.getStartDate().minusYears(2));
            johnLoanRequest.setLoanAmount(BigDecimal.valueOf(3000000));
            johnSavingsAccount.setAccountLoanRequest(johnLoanRequest);

        }catch(MavenBankException ex){
            ex.printStackTrace();
        }
    }

    @Test
    void approveLoanWithAccountBalanceAndHighLoanRequestAmount() throws MavenBankException {
        try{
            Account johnCurrentAccount = accountService. findAccount(2);
            assertNull(johnCurrentAccount.getAccountLoanRequest());
            johnLoanRequest.setLoanAmount(BigDecimal.valueOf(90000000));
            johnCurrentAccount.setAccountLoanRequest(johnLoanRequest);

            LoanRequest processedLoanRequest = loanService.approveLoanRequest(johnCurrentAccount);
            assertNotNull(processedLoanRequest);
            assertEquals(LoanRequestStatus.PENDING, processedLoanRequest.getLoanStatus());

        }catch(MavenBankException ex){
            ex.printStackTrace();
        }
    }
}