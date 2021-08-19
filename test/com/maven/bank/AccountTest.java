package com.maven.bank;

import com.maven.bank.bankingservices.BankService;
import com.maven.bank.datastore.AccountType;
import com.maven.bank.datastore.CustomerRepo;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.entities.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTest {
    Customer john;
    Account johnSavingsAccount;


    @BeforeEach
    void setUp(){
     john = new Customer();
     johnSavingsAccount = new SavingsAccount();
    }

    @Test
    void openAccount(){
        john.setBVN(BankService.generateBVN());
        john.setEmail("John@Doe.com");
        john.setFirstName("John");
        john.setSurName("Doe");
        john.setPhone("7687687687687");


        johnSavingsAccount.setAccountNumber(BankService.generateAccountNumber());
        johnSavingsAccount.setBalance(new BigDecimal(5000));
        johnSavingsAccount.setAccountPin("1000");
        john.getAccounts().add(johnSavingsAccount);
        assertFalse(CustomerRepo.getCustomers().isEmpty());

    }
}
