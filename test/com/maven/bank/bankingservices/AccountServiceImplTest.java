package com.maven.bank.bankingservices;

import com.maven.bank.datastore.*;
import com.maven.bank.entities.*;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientFundsException;
import com.maven.bank.exceptions.MavenBankTransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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


        try{
            long newAccountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);
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
        assertThrows(MavenBankException.class, ()->accountService.openAccount(null, AccountType.SAVINGSACCOUNT));
    }

    @Test
    void openAccountWithNoAccountType(){
        assertThrows(MavenBankException.class, ()->accountService.openAccount(bessie, null));
    }

    @Test
    void openSameTypeOfAccountForTheSameCustomer(){

        Optional<Customer>bessieOptional = CustomerRepo.getCustomers().values().stream().findFirst();
        Customer bessie = ( bessieOptional.isEmpty()) ? null : bessieOptional.get();
        assertEquals(3, BankService.getCurrentAccountNumber());

        assertNotNull(bessie);
        assertNotNull(bessie.getAccounts());

        assertFalse(bessie.getAccounts().isEmpty());
        assertEquals(AccountType.SAVINGSACCOUNT.toString(),
                bessie.getAccounts().get(0).getClass().getSimpleName().toUpperCase());

        assertThrows(MavenBankException.class, ()->accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT));
        assertEquals(3, BankService.getCurrentAccountNumber());
        assertEquals(2, bessie.getAccounts().size());
    }


    @Test
    void openAccountForCurrentAccount(){
        assertFalse(CustomerRepo.getCustomers().isEmpty());
        assertEquals(3, BankService.getCurrentAccountNumber());
        assertFalse(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));

        try{
            long newAccountNumber = accountService.openAccount(bessie, AccountType.CURRENTACCOUNT);
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
            long newAccountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);

            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(4, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));
            assertEquals(1, bessie.getAccounts().size());
            assertEquals(newAccountNumber, bessie.getAccounts().get(0).getAccountNumber());

            newAccountNumber = accountService.openAccount(bessie, AccountType.CURRENTACCOUNT);
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
            long newAccountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);
            assertFalse(CustomerRepo.getCustomers().isEmpty());
            assertEquals(4, BankService.getCurrentAccountNumber());
            assertTrue(CustomerRepo.getCustomers().containsKey(bessie.getBvn()));
            assertFalse(bessie.getAccounts().isEmpty());
            assertEquals(newAccountNumber, bessie.getAccounts().get(0).getAccountNumber());
            assertEquals(3, CustomerRepo.getCustomers().size());


            assertThrows(MavenBankException.class, () -> accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT));

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
            long accountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);
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
           long accountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);
            Account bessieSavingsAccount = accountService.findAccount(accountNumber);
            assertEquals(BigDecimal.ZERO, bessieSavingsAccount.getBalance());
            System.out.println(bessieSavingsAccount.getBalance());
            BigDecimal depositAmount = new BigDecimal("1000000000000000000");
            BigDecimal accountBalance = accountService.deposit(depositAmount, accountNumber);

            bessieSavingsAccount = accountService.findAccount(accountNumber);
            assertEquals(accountBalance, bessieSavingsAccount.getBalance());
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

//    @Test
//    void depositWithVeryLargeAmount(){
//        try{
//            long accountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);
//            Account bessieSavingsAccount = accountService.findAccount (accountNumber);
//            assertEquals (BigDecimal.ZERO, bessieSavingsAccount.getBalance ());
//            BigDecimal depositAmount = new BigDecimal ("10000000000000000000");
//
//            BigDecimal accountBalance = accountService.deposit (depositAmount, accountNumber);
//
//            bessieSavingsAccount = accountService.findAccount (accountNumber);
//            assertEquals (accountBalance, bessieSavingsAccount.getBalance ());
//
//        } catch (MavenBankTransactionException ex) {
//            ex.printStackTrace ();
//        }catch (MavenBankException ex){
//            ex.printStackTrace ();
//        }
//    }


    @Test
    void withdraw(){
        try{
            long accountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);
            Account bessieSavingsAccount = accountService.findAccount(accountNumber);
            assertEquals(BigDecimal.ZERO, bessieSavingsAccount.getBalance());
            BigDecimal depositAmount = accountService.deposit(BigDecimal.valueOf(10000), accountNumber);
            assertEquals(depositAmount, bessieSavingsAccount.getBalance());

            bessieSavingsAccount = accountService.findAccount(accountNumber);
            accountService.withdraw(BigDecimal.valueOf(5000),accountNumber);
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
            BigDecimal accountBalance = accountService.deposit (new BigDecimal (50000), johnSavingsAccount.getAccountNumber());

            assertThrows (MavenBankInsufficientFundsException.class,
                    () -> accountService.withdraw(BigDecimal.valueOf(70000),1));

            assertEquals (accountBalance, johnSavingsAccount.getBalance ());
        }catch (MavenBankInsufficientFundsException e) {
            e.printStackTrace ();
        }catch (MavenBankException e){
            e.printStackTrace();
        }

    }

    @Test
    void applyForLoan(){
        LoanRequest johnLoanRequest = new LoanRequest();
        johnLoanRequest.setLoanAmount(BigDecimal.valueOf(50000000));
        johnLoanRequest.setApplyDate(LocalDateTime.now());
        johnLoanRequest.setTypeOfLoan(LoanType.SME);
        johnLoanRequest.setInterestRate(0.1);
        johnLoanRequest.setLoanStatus(LoanRequestStatus.NEW);
        johnLoanRequest.setTenor(24);
                try{
                    Account johnCurrentAccount = accountService.findAccount(2);
                    assertNull(johnCurrentAccount.getAccountLoanRequest());
                    johnCurrentAccount.setAccountLoanRequest(johnLoanRequest);
                    assertNotNull(johnCurrentAccount.getAccountLoanRequest());
                    LoanRequestStatus decision = accountService.applyForLoan(johnCurrentAccount);
                    assertNull(decision);
                }
                catch(MavenBankException ex){
                    ex.printStackTrace();
                }
    }

    @Test
    void addBankTransactionWillNullTransaction() throws MavenBankException {
        long accountNumber = accountService.openAccount(bessie, AccountType.SAVINGSACCOUNT);
        assertThrows(MavenBankTransactionException.class, ()-> accountService.addBankTransaction(null,
                accountService.findAccount(bessie, accountNumber)));
    }

    @Test
    void addBankTransactionWillNullAccount(){
        BankTransaction transaction = new BankTransaction(BankTransactionType.DEPOSIT, BigDecimal.valueOf(50));
            assertThrows(MavenBankTransactionException.class, () -> accountService.addBankTransaction(transaction,null));
    }

    @Test
    void addBankTransactionWithDeposit(){
        try {
            Account janeSavingsAccount = accountService.findAccount(3);
            assertNotNull(janeSavingsAccount);
            assertEquals(BigDecimal.ZERO, janeSavingsAccount.getBalance());
            assertEquals(0, janeSavingsAccount.getTransactions().size());
            BankTransaction janeDeposit = new BankTransaction(BankTransactionType.DEPOSIT, BigDecimal.valueOf(10000));
            accountService.addBankTransaction(janeDeposit, janeSavingsAccount);
            assertEquals(BigDecimal.valueOf(10000), janeSavingsAccount.getBalance());
            assertEquals(1, janeSavingsAccount.getTransactions().size());
        }catch(MavenBankException ex){
            ex.printStackTrace();
        }
    }

    @Test
    void addBankTransactionWithNegativeDeposit() {
        try {
            Account janeSavingsAccount = accountService.findAccount(3);
            assertNotNull(janeSavingsAccount);
            assertEquals(BigDecimal.ZERO, janeSavingsAccount.getBalance());
            assertEquals(0, janeSavingsAccount.getTransactions().size());

            BankTransaction janeDeposit = new BankTransaction(BankTransactionType.DEPOSIT, BigDecimal.valueOf(-10000));

            assertThrows(MavenBankTransactionException.class,
                         ()-> accountService.addBankTransaction(janeDeposit,janeSavingsAccount));
            assertEquals(BigDecimal.ZERO, janeSavingsAccount.getBalance());
            assertEquals(0, janeSavingsAccount.getTransactions().size());
        } catch (MavenBankException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void addBankTransactionForWithdrawal(){
        try{
            Account janeSavingsAccount = accountService.findAccount(3);
            assertNotNull(janeSavingsAccount);
            BankTransaction depositTx = new BankTransaction(BankTransactionType.DEPOSIT, BigDecimal.valueOf(50000));
            accountService.addBankTransaction(depositTx, janeSavingsAccount);
            assertEquals(BigDecimal.valueOf(50000), janeSavingsAccount.getBalance());
            assertEquals(1, janeSavingsAccount.getTransactions().size());

            BankTransaction withdrawalTx = new BankTransaction(BankTransactionType.WITHDRAWAL,
                    BigDecimal.valueOf(30000));
            accountService.addBankTransaction(withdrawalTx, janeSavingsAccount);
            assertEquals(BigDecimal.valueOf(20000), janeSavingsAccount.getBalance());
            assertEquals(2, janeSavingsAccount.getTransactions().size());


        }catch(MavenBankTransactionException ex){
            ex.printStackTrace();
        } catch (MavenBankException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addBankTransactionForNegativeWithdraw() throws MavenBankException {
        try{
            Account janeSavingAccount = accountService.findAccount(3);
            assertNotNull(janeSavingAccount);

            assertEquals(BigDecimal.ZERO, janeSavingAccount.getBalance());
            assertEquals(0, janeSavingAccount.getTransactions().size());

            BankTransaction withdrawTx = new BankTransaction(BankTransactionType.WITHDRAWAL, BigDecimal.valueOf(-50000));
            assertThrows(MavenBankTransactionException.class,
                    () -> accountService.addBankTransaction(withdrawTx, janeSavingAccount));
            assertEquals(BigDecimal.ZERO,janeSavingAccount.getBalance() );
            assertEquals(0,janeSavingAccount.getTransactions().size() );

        }catch(MavenBankException ex){
            ex.printStackTrace();
        }

    }
}

