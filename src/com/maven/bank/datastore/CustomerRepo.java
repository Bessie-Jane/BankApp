package com.maven.bank.datastore;

import com.maven.bank.entities.Account;
import com.maven.bank.entities.CurrentAccount;
import com.maven.bank.entities.Customer;
import com.maven.bank.entities.SavingsAccount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CustomerRepo {
    private static Map<Long, Customer> customers = new HashMap<>();

    public static Map<Long, Customer> getCustomers() {
        return customers;
    }

    private static void setCustomers(Map<Long, Customer> customers){
        CustomerRepo.customers = customers;
    }
    static {
        reset();
    }

    public static void reset() {
        Customer john = new Customer();
        john.setBVN(1);
        john.setEmail("John@Doe.com");
        john.setFirstName("John");
        john.setSurName("Doe");
        john.setPhone("7687687687687");
        Account johnSavingsAccount = new SavingsAccount(1);
        john.setRelationshipStartDate(johnSavingsAccount.getStartDate());
        john.getAccounts().add(johnSavingsAccount);

        Account johnCurrentAccount = new CurrentAccount(2, BigDecimal.valueOf(50_000_000));
        john.getAccounts().add(johnCurrentAccount);
        customers.put(john.getBvn(), john);


        Customer jane = new Customer();
        jane.setBVN(2);
        jane.setEmail("jane@bessie.com");
        jane.setFirstName("jane");
        jane.setSurName("bush");
        jane.setPhone("5645645645645");

        Account janeSavingsAccount = new SavingsAccount(3);
        jane.setRelationshipStartDate(janeSavingsAccount.getStartDate());
        jane.getAccounts().add(janeSavingsAccount);
        customers.put(jane.getBvn(), jane);

    }

    public static void tearDown(long bvn){
        customers.remove(bvn);
    }

//    public static void createRepo(){
//        customers = new HashMap<>();
//    }

}
