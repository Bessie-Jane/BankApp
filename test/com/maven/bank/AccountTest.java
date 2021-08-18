package com.maven.bank;

import com.maven.bank.bankingservices.BankService;
import com.maven.bank.datastore.AccountType;
import com.maven.bank.datastore.CustomerRepo;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTest {
    Customer john;
    Account johnAccount;


    @BeforeEach
    void setUp(){
     john = new Customer();
     johnAccount = new Account();
    }

    @Test
    void openAccount(){
        john.setBVN(BankService.generateBVN());
        john.setEmail("John@Doe.com");
        john.setFirstName("John");
        john.setSurName("Doe");
        john.setPhone("7687687687687");


        johnAccount.setAccountNumber(BankService.generateAccountNumber());
        johnAccount.setTypeOfAccount(AccountType.SAVINGS);
        johnAccount.setBalance(new BigDecimal(5000));
        johnAccount.setAccountPin("1000");
        john.getAccounts().add(johnAccount);
        assertFalse(CustomerRepo.getCustomers().isEmpty());

    }
}
