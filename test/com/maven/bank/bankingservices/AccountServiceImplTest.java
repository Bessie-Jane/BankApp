package com.maven.bank.bankingservices;

import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.datastore.AccountType;
import com.maven.bank.datastore.CustomerRepo;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientFundsException;
import com.maven.bank.exceptions.MavenBankTransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {
    private AccountService accountService;
    private Customer bessie;


    @BeforeEach
    void setUp(){
        accountService = new AccountServiceImpl();
        bessie = new Customer();
        bessie.setBVN(BankService.generateBVN());
        bessie.setEmail("bessie@bessie.com");
        bessie.setFirstName("bessie");
        bessie.setSurName("bush");
        bessie.setPhone("5645645645645");
    }

    @AfterEach
    void tearDown() {
      BankService.tearDownMethod();
      CustomerRepo.tearDown(bessie.getBvn());
      CustomerRepo.reset();
    }
    

    @Test
    void openSavingsAccount(){
        assertFalse(CustomerRepo.getCustomers().isEmpty());
        assertEquals(3, BankService.getCurrentAccountNumber());
        assertFalse(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));

        try{
            long newAccountNumber = accountService.openAccount(bessie, AccountType.SAVINGS);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(4, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));
            assertFalse(bessie.getAccounts().isEmpty());
            assertEquals(newAccountNumber, bessie.getAccounts().get(0).getAccountNumber());
        }catch (MavenBankException ex){
            ex.printStackTrace();
        }

    }

    @Test
    void openAccountWithNoCustomer(){
        assertThrows(MavenBankException.class, ()->accountService.openAccount(null, AccountType.SAVINGS));
    }

    @Test
    void openAccountWithNoAccountType(){
        assertThrows(MavenBankException.class, ()->accountService.openAccount(bessie, null));
    }

    @Test
    void openSameTypeOfAccountForTheSameCustomer(){
//        System.out.println(CustomerRepo.getCustomers());
        Optional<Customer>bessieOptional = CustomerRepo.getCustomers().values().stream().findFirst();
        Customer bessie = ( bessieOptional.isEmpty()) ? null : bessieOptional.get();
        assertEquals(3, BankService.getCurrentAccountNumber());

        assertNotNull(bessie);
        assertNotNull(bessie.getAccounts());

        assertFalse(bessie.getAccounts().isEmpty());
        assertEquals(AccountType.SAVINGS, bessie.getAccounts().get(0).getTypeOfAccount());

        assertThrows(MavenBankException.class, ()->accountService.openAccount(bessie, AccountType.SAVINGS));
        assertEquals(3, BankService.getCurrentAccountNumber());
        assertEquals(2, bessie.getAccounts().size());
    }


    @Test
    void openAccountForCurrentAccount(){
        assertFalse(CustomerRepo.getCustomers().isEmpty());
        assertEquals(3, BankService.getCurrentAccountNumber());
        assertFalse(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));

        try{
            long newAccountNumber = accountService.openAccount(bessie, AccountType.CURRENT);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(4, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));
            assertFalse(bessie.getAccounts().isEmpty());
            assertEquals(newAccountNumber, bessie.getAccounts().get(0).getAccountNumber());
        }catch (MavenBankException ex){
            ex.printStackTrace();
        }
    }

    @Test
    void openDifferentTypeOfAccountForTheSameCustomer(){
        assertFalse(CustomerRepo.getCustomers().isEmpty());
        assertEquals(3, BankService.getCurrentAccountNumber());
        assertFalse(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));

        try{
            long newAccountNumber = accountService.openAccount(bessie, AccountType.SAVINGS);

            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(4, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));
            assertEquals(1, bessie.getAccounts().size());
            assertEquals(newAccountNumber, bessie.getAccounts().get(0).getAccountNumber());

            newAccountNumber = accountService.openAccount(bessie, AccountType.CURRENT);
            assertEquals(5, BankService.getCurrentAccountNumber());
            assertEquals(2, bessie.getAccounts().size());
            assertEquals(newAccountNumber, bessie.getAccounts().get(1).getAccountNumber());

        }catch (MavenBankException ex){
            ex.printStackTrace();
        }

    }

    @Test
    void openSavingsAccountForNewCustomer(){
        assertFalse(CustomerRepo.getCustomers().isEmpty());
        assertEquals(3, BankService.getCurrentAccountNumber());
        assertFalse(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));

        try{
            long newAccountNumber = accountService.openAccount(bessie, AccountType.SAVINGS);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(4, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));
            assertFalse(bessie.getAccounts().isEmpty());
            assertEquals(newAccountNumber, bessie.getAccounts().get(0).getAccountNumber());
            assertEquals(3, CustomerRepo.getCustomers().size());


            assertThrows(MavenBankException.class, () -> accountService.openAccount(bessie, AccountType.SAVINGS));

            assertEquals(3, CustomerRepo.getCustomers().size());
            assertEquals(4, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));
            assertFalse(bessie.getAccounts().isEmpty());
            assertEquals(1, bessie.getAccounts().size());
            assertEquals(newAccountNumber, bessie.getAccounts().get(0).getAccountNumber());

        }catch (MavenBankException ex){
            ex.printStackTrace();
        }

    }

    @Test
    void findAccount(){
        try {
            Account bessieCurrentAccount= accountService.findAccount(2);
            assertNotNull(bessieCurrentAccount);
            assertEquals(2, bessieCurrentAccount.getAccountNumber());
        } catch (MavenBankException ex) {
            ex.printStackTrace();
        }

    }

    @Test
    void findAccountWithInvalidAccountNumber(){
        try{
            Account johnCurrentAccount = accountService.findAccount(5);
            assertNull(johnCurrentAccount);
        }catch (MavenBankException ex){
            ex.printStackTrace();
        }
    }

    @Test
    void deposit() {
        try {
            long accountNumber = accountService.openAccount(bessie, AccountType.SAVINGS);
            Account bessieSavingsAccount = accountService.findAccount(accountNumber);
            assertEquals(BigDecimal.ZERO, bessieSavingsAccount.getBalance());

            BigDecimal accountBalance = accountService.deposit(new BigDecimal(50000), accountNumber);
            bessieSavingsAccount = accountService.findAccount(accountNumber);

            assertEquals(new BigDecimal(50000),bessieSavingsAccount.getBalance());
        } catch (MavenBankException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    void depositVeryLargeDepositAmount(){
        try{
            long accountNumber = accountService.openAccount(bessie, AccountType.SAVINGS);
            Account bessieSavingsAccount = accountService.findAccount(accountNumber);
            assertEquals(BigDecimal.ZERO, bessieSavingsAccount.getBalance());
            BigDecimal depositAmount = new BigDecimal("1000000000000000000");
            BigDecimal accountBalance = accountService.deposit(depositAmount, 1);

            bessieSavingsAccount = accountService.findAccount(1);
            assertEquals(depositAmount, bessieSavingsAccount.getBalance());
        }catch(MavenBankTransactionException ex){
            ex.printStackTrace();
        }catch (MavenBankException ex){
            ex.printStackTrace();
        }
    }


    @Test
    void depositNegativeAmount(){
        assertThrows(MavenBankTransactionException.class, ()->accountService.deposit(new BigDecimal(-5000), 1));
    }

    @Test
    void depositToInvalidAccountNumber(){
        assertThrows (MavenBankException.class,
                () -> accountService.deposit (new BigDecimal (5000), 5));

    }

    @Test
    void depositWithVeryLargeAmount(){
        try{
            Account bessieSavingsAccount = accountService.findAccount (1);
            assertEquals (BigDecimal.ZERO, bessieSavingsAccount.getBalance ());
            BigDecimal depositAmount = new BigDecimal ("10000000000000000000");

            BigDecimal accountBalance = accountService.deposit (depositAmount, 1);

            bessieSavingsAccount = accountService.findAccount (1);
            assertEquals (depositAmount, bessieSavingsAccount.getBalance ());

        } catch (MavenBankTransactionException ex) {
            ex.printStackTrace ();
        }catch (MavenBankException ex){
            ex.printStackTrace ();
        }
    }


    @Test
    void withdraw(){
        try{
            Account bessieSavingsAccount = accountService.findAccount(1);
            assertEquals(BigDecimal.ZERO, bessieSavingsAccount.getBalance());
            BigDecimal depositAmount = accountService.deposit(BigDecimal.valueOf(10000), 1);
            assertEquals(depositAmount, bessieSavingsAccount.getBalance());

            bessieSavingsAccount = accountService.findAccount(1);
            accountService.withdraw(BigDecimal.valueOf(5000),1);
            assertEquals(BigDecimal.valueOf(5000),bessieSavingsAccount.getBalance());

        }catch (MavenBankException ex){
            ex.printStackTrace();
        }

    }

    @Test
    void withdrawNegativeAmount(){
        assertThrows(MavenBankTransactionException.class,
                () -> accountService.withdraw(new BigDecimal(-5000), 1));
    }


    @Test
    void withdrawFromAnInvalidAccount() throws MavenBankException {
        assertThrows (MavenBankException.class,
                () -> accountService.withdraw(new BigDecimal (5000), 15));
    }

    @Test
    void withdrawAmountHigherThanAccountBalance()  {
        try{
            Account johnSavingsAccount = accountService.findAccount (1);
            BigDecimal accountBalance = accountService.deposit (new BigDecimal (50000), 1);

            assertEquals (accountBalance, johnSavingsAccount.getBalance ());
        } catch (MavenBankException e) {
            e.printStackTrace ();
        }
        assertThrows (MavenBankInsufficientFundsException.class,
                () -> accountService.withdraw(new BigDecimal (70000), 1));
    }
}

